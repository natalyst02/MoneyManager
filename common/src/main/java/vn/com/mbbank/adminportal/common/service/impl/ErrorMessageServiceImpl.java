package vn.com.mbbank.adminportal.common.service.impl;

import com.google.common.base.Throwables;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.mapper.Entity2ErrorMessageResponseMapper;
import vn.com.mbbank.adminportal.common.model.ErrorMessageStatus;
import vn.com.mbbank.adminportal.common.model.entity.ErrorMessage;
import vn.com.mbbank.adminportal.common.model.filter.ErrorMessageFilter;
import vn.com.mbbank.adminportal.common.model.response.ErrorMessageResponse;
import vn.com.mbbank.adminportal.common.repository.ErrorMessageRepository;
import vn.com.mbbank.adminportal.common.service.internal.ErrorMessageServiceInternal;
import vn.com.mbbank.adminportal.common.util.*;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
@Service
public class ErrorMessageServiceImpl implements ErrorMessageServiceInternal {
  private final ErrorMessageRepository errorMessageRepository;
  private final TransactionTemplate transactionTemplate;
  private final Entity2ErrorMessageResponseMapper errorMessageResponseMapper;
  private final List<ErrorMessageProcessor> processors = new CopyOnWriteArrayList<>();

  @Override
  public ErrorMessage save(String topic, int partition, long offset, String key, String message, Throwable cause) {
    var id = errorMessageRepository.nextErrorMessageId();
    var error = Throwables.getStackTraceAsString(cause);
    var now = OffsetDateTime.now();
    var errorMessage = createErrorMessage(id, topic, partition, offset, key, message, error, now);
    return errorMessageRepository.persist(errorMessage);
  }

  @Override
  public List<ErrorMessage> save(ConsumerRecords<String, byte[]> records, Throwable cause) {
    var total = records.count();
    List<Long> ids = errorMessageRepository.nextErrorMessageIds(total);
    var errorMessages = new ArrayList<ErrorMessage>(total);
    var error = Throwables.getStackTraceAsString(cause);
    var index = 0;
    Long id;
    var now = OffsetDateTime.now();
    for (var rec : records) {
      id = ids.get(index++);
      errorMessages.add(
          createErrorMessage(id, rec.topic(), rec.partition(), rec.offset(), rec.key(), rec.value(), error, now)
      );
    }
    return errorMessageRepository.persistAll(errorMessages);
  }

  @Override
  public List<ErrorMessage> save(ConsumerRecords<String, byte[]> records, List<Throwable> causes) {
    var total = (int) causes.stream().filter(Objects::nonNull).count();
    var ids = errorMessageRepository.nextErrorMessageIds(total);
    var errorMessages = new ArrayList<ErrorMessage>(total);
    var index = 0;
    var idIndex = 0;
    Throwable cause;
    String error;
    Long id;
    var now = OffsetDateTime.now();
    for (var rec : records) {
      cause = causes.get(index++);
      if (cause == null) {
        continue;
      }
      id = ids.get(idIndex++);
      error = Throwables.getStackTraceAsString(cause);
      errorMessages.add(
          createErrorMessage(id, rec.topic(), rec.partition(), rec.offset(), rec.key(), rec.value(), error, now)
      );
    }
    return errorMessageRepository.persistAll(errorMessages);
  }

  @Override
  public List<ErrorMessage> save(List<ConsumerRecord<String, byte[]>> records, List<Throwable> causes) {
    var total = records.size();
    var ids = errorMessageRepository.nextErrorMessageIds(total);
    var errorMessages = new ArrayList<ErrorMessage>(total);
    var index = 0;
    Throwable cause;
    String error;
    Long id;
    var now = OffsetDateTime.now();
    for (var rec : records) {
      id = ids.get(index);
      cause = causes.get(index++);
      error = Throwables.getStackTraceAsString(cause);
      errorMessages.add(
          createErrorMessage(id, rec.topic(), rec.partition(), rec.offset(), rec.key(), rec.value(), error, now)
      );
    }
    return errorMessageRepository.persistAll(errorMessages);
  }

  @SuppressWarnings("java:S107")
  private ErrorMessage createErrorMessage(Long id, String topic, int partition, long offset, String key, byte[] message, String error, OffsetDateTime createdAt) {
    return createErrorMessage(id, topic, partition, offset, key, new String(message, StandardCharsets.UTF_8), error, createdAt);
  }

  @SuppressWarnings("java:S107")
  private ErrorMessage createErrorMessage(Long id, String topic, int partition, long offset, String key, String message, String error, OffsetDateTime createdAt) {
    return new ErrorMessage()
        .setId(id)
        .setTopic(topic)
        .setPartition(partition)
        .setOffset(offset)
        .setKey(key)
        .setMessage(message)
        .setError(error)
        .setStatus(ErrorMessageStatus.INIT)
        .setCreatedBy(Constant.SYSTEM_USER)
        .setCreatedAt(createdAt)
        .setUpdatedBy(Constant.SYSTEM_USER)
        .setUpdatedAt(createdAt);
  }

  @Override
  public ErrorMessage getErrorMessage0(Long id) {
    return errorMessageRepository.findById(id)
        .orElseThrow(() -> new PaymentPlatformException(CommonErrorCode.ERROR_MESSAGE_NOT_FOUND, "Error message: " + id + " not found"));
  }

  @Override
  public boolean process(Long id) {
    var exceptionHolder = new DataHolder<RuntimeException>();
    var result = transactionTemplate.execute(status -> {
      var errorMsg = errorMessageRepository.getLockedErrorMessage(id, ErrorMessageStatus.INIT)
          .orElseThrow(() -> new PaymentPlatformException(CommonErrorCode.ERROR_MESSAGE_NOT_FOUND, "Error message: " + id + " with status 'INIT' not found"));
      try {
        if (doProcess(errorMsg)) {
          errorMessageRepository.setStatus(id, ErrorMessageStatus.INIT, ErrorMessageStatus.DONE, OffsetDateTime.now());
          return true;
        }
      } catch (RuntimeException e) {
        exceptionHolder.setValue(e);
        errorMessageRepository.setError(id, Throwables.getStackTraceAsString(e), OffsetDateTime.now());
      }
      return false;
    });
    if (exceptionHolder.getValue() != null) {
      throw exceptionHolder.getValue();
    }
    return Boolean.TRUE.equals(result);
  }

  @Override
  public ErrorMessageResponse getErrorMessage(Long id) {
    return errorMessageResponseMapper.map(getErrorMessage0(id));
  }

  private boolean doProcess(ErrorMessage errorMsg) {
    return CompletableFuture.supplyAsync(() -> {
      for (var processor : processors) {
        if (processor.process(errorMsg)) {
          return true;
        }
      }
      return false;
    }).join();
  }

  @Override
  public Page<ErrorMessageResponse> getErrorMessages(ErrorMessageFilter filter, Pageable pageable) {
    return errorMessageRepository.findAll(Filters.toSpecification(filter), pageable)
        .map(errorMessageResponseMapper::map);
  }

  @Override
  public boolean discard(Long id) {
    return errorMessageRepository.setStatus(id, ErrorMessageStatus.INIT, ErrorMessageStatus.DISCARDED, OffsetDateTime.now()) > 0;
  }

  @Override
  public ErrorMessageServiceInternal addProcessor(ErrorMessageProcessor processor) {
    processors.add(Objects.requireNonNull(processor));
    return this;
  }

  @Override
  public void dropPartitions(Integer olderMonths) {
    errorMessageRepository.dropPartitions(olderMonths);
  }
}
