package vn.com.mbbank.adminportal.common.service.impl;

import com.dslplatform.json.JsonWriter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.entity.EventMessage;
import vn.com.mbbank.adminportal.common.model.request.CreateEventMessageRequest;
import vn.com.mbbank.adminportal.common.repository.EventMessageRepository;
import vn.com.mbbank.adminportal.common.service.internal.EventMessageServiceInternal;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.common.util.Json;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Log4j2
@Service
public class EventMessageServiceImpl implements EventMessageServiceInternal {
  private final EventMessageRepository eventMessageRepository;
  private final TransactionTemplate transactionTemplate;
  private final KafkaTemplate<String, byte[]> kafkaTemplate;
  private final KafkaTemplate<String, byte[]> kafka9093Template;

  public EventMessageServiceImpl(EventMessageRepository eventMessageRepository,
                                 TransactionTemplate transactionTemplate,
                                 @Qualifier("kafkaTemplate") KafkaTemplate<String, byte[]> kafkaTemplate,
                                 @Qualifier("kafka9093Template") KafkaTemplate<String, byte[]> kafka9093Template) {
    this.eventMessageRepository = eventMessageRepository;
    this.transactionTemplate = transactionTemplate;
    this.kafkaTemplate = kafkaTemplate;
    this.kafka9093Template = kafka9093Template;
  }

  @Override
  public EventMessage save(String topic, String key, byte[] message) {
    var id = eventMessageRepository.nextEventMessageId();
    var now = OffsetDateTime.now();
    var eventMessage = new EventMessage(topic, key, message)
        .setId(id)
        .setCreatedBy(Constant.SYSTEM_USER)
        .setCreatedAt(now)
        .setUpdatedBy(Constant.SYSTEM_USER)
        .setUpdatedAt(now);
    return eventMessageRepository.persist(eventMessage);
  }

  @Override
  public EventMessage save(String topic, String key, byte[] message, Integer partition) {
    var id = eventMessageRepository.nextEventMessageId();
    var now = OffsetDateTime.now();
    var eventMessage = new EventMessage(topic, key, message)
        .setId(id)
        .setPartition(partition)
        .setCreatedBy(Constant.SYSTEM_USER)
        .setCreatedAt(now)
        .setUpdatedBy(Constant.SYSTEM_USER)
        .setUpdatedAt(now);
    return eventMessageRepository.persist(eventMessage);
  }

  @Override
  public EventMessage save(String topic, String key, String message) {
    var id = eventMessageRepository.nextEventMessageId();
    var now = OffsetDateTime.now();
    var eventMessage = new EventMessage(topic, key, message)
        .setId(id)
        .setCreatedBy(Constant.SYSTEM_USER)
        .setCreatedAt(now)
        .setUpdatedBy(Constant.SYSTEM_USER)
        .setUpdatedAt(now);
    return eventMessageRepository.persist(eventMessage);
  }

  @Override
  public EventMessage save(String topic, String key, String userName, byte[] message) {
    var id = eventMessageRepository.nextEventMessageId();
    var createdAt = OffsetDateTime.now();
    var eventMessage = new EventMessage(topic, key, message)
        .setId(id)
        .setCreatedBy(userName)
        .setCreatedAt(createdAt)
        .setUpdatedBy(userName)
        .setUpdatedAt(createdAt);
    return eventMessageRepository.persist(eventMessage);
  }

  @Override
  public EventMessage save(String topic, EventMessage eventMessage) {
    var id = eventMessageRepository.nextEventMessageId();
    var newEventMsg = new EventMessage(id, topic, eventMessage);
    return eventMessageRepository.persistAndFlush(newEventMsg);
  }

  @Override
  public EventMessage save(String topic, String key, String message, Integer partition) {
    var id = eventMessageRepository.nextEventMessageId();
    var now = OffsetDateTime.now();
    var eventMessage = new EventMessage(topic, key, message)
        .setId(id)
        .setPartition(partition)
        .setCreatedBy(Constant.SYSTEM_USER)
        .setCreatedAt(now)
        .setUpdatedBy(Constant.SYSTEM_USER)
        .setUpdatedAt(now);
    return eventMessageRepository.persist(eventMessage);
  }

  @Override
  public List<EventMessage> save(List<CreateEventMessageRequest> requests) {
    var total = requests.size();
    var ids = eventMessageRepository.nextEventMessageIds(total);
    var now = OffsetDateTime.now();
    var eventMessages = new ArrayList<EventMessage>(total);
    for (var i = 0; i < total; ++i) {
      var request = requests.get(i);
      eventMessages.add(new EventMessage(request.getTopic(),
          request.getKey(),
          request.getMessage())
          .setId(ids.get(i))
          .setPartition(request.getPartition())
          .setCreatedBy(Constant.SYSTEM_USER)
          .setCreatedAt(now)
          .setUpdatedBy(Constant.SYSTEM_USER)
          .setUpdatedAt(now));
    }
    return eventMessageRepository.persistAll(eventMessages);
  }

  @Transactional(readOnly = true)
  @Override
  public Stream<EventMessage> getUnsentEventMessages(String topic) {
    return eventMessageRepository.getUnsentEventMessages(topic);
  }

  @Transactional(readOnly = true)
  @Override
  public Stream<EventMessage> getUnsentEventMessages(String topic, OffsetDateTime createdAtTo) {
    return eventMessageRepository.getUnsentEventMessages(topic, createdAtTo);
  }

  @Transactional(readOnly = true)
  @Override
  public Stream<EventMessage> getUnsentEventMessages(List<String> topics, OffsetDateTime createdAtTo) {
    return eventMessageRepository.getUnsentEventMessages(topics, createdAtTo);
  }

  @Override
  public CompletableFuture<SendResult<String, byte[]>> sendAsync(String topic, String key, byte[] message) {
    var context = ThreadContext.getImmutableContext();
    return kafkaTemplate.send(topic, key, message)
        .whenComplete((v, e) -> {
          ThreadContext.clearMap();
          ThreadContext.putAll(context);
        });
  }

  @Override
  public CompletableFuture<SendResult<String, byte[]>> sendAsync(String topic, String key, byte[] message, Integer partition) {
    var context = ThreadContext.getImmutableContext();
    return kafkaTemplate.send(topic, partition, key, message)
        .whenComplete((v, e) -> {
          ThreadContext.clearMap();
          ThreadContext.putAll(context);
        });
  }

  @Transactional(readOnly = true)
  @Override
  public void send(String topic) {
    try (var stream = eventMessageRepository.getUnsentEventMessages(topic)) {
      stream.forEach(msg -> sendAndUpdateAsync(msg)
          .whenComplete((v, t) -> {
            if (t != null) {
              log.error("Can't send event message wih key: {} to topic: {}", msg.getKey(), msg.getTopic(), t);
            }
          }));
    }
  }

  @Transactional(readOnly = true)
  @Override
  public void send(String topic, OffsetDateTime createAtTo) {
    try (var stream = eventMessageRepository.getUnsentEventMessages(topic, createAtTo)) {
      stream.forEach(msg -> sendAndUpdateAsync(msg)
          .whenComplete((v, t) -> {
            if (t != null) {
              log.error("Can't send event message wih key: {} to topic: {}", msg.getKey(), msg.getTopic(), t);
            }
          }));
    }
  }

  @Transactional(readOnly = true)
  @Override
  public void send(List<String> topics, OffsetDateTime createAtTo) {
    try (var stream = eventMessageRepository.getUnsentEventMessages(topics, createAtTo)) {
      stream.forEach(msg -> sendAndUpdateAsync(msg)
          .whenComplete((v, t) -> {
            if (t != null) {
              log.error("Can't send event message wih key: {} to topic: {}", msg.getKey(), msg.getTopic(), t);
            }
          }));
    }
  }

  @SuppressWarnings("java:S2229")
  @Override
  public CompletableFuture<Void> sendAsync(String topic) {
    return CompletableFuture.runAsync(() -> transactionTemplate.execute(status -> {
      send(topic);
      return null;
    }));
  }

  @SuppressWarnings("java:S2229")
  @Override
  public CompletableFuture<Void> sendAsync(String topic, OffsetDateTime createAtTo) {
    return CompletableFuture.runAsync(() -> transactionTemplate.execute(status -> {
      send(topic, createAtTo);
      return null;
    }));
  }

  @SuppressWarnings("java:S2229")
  @Override
  public CompletableFuture<Void> sendAsync(List<String> topics, OffsetDateTime createAtTo) {
    return CompletableFuture.runAsync(() -> transactionTemplate.execute(status -> {
      send(topics, createAtTo);
      return null;
    }));
  }

  @Override
  public CompletableFuture<Void> sendAndUpdateAsync(EventMessage eventMessage) {
    return sendAsync(eventMessage)
        .thenApply(res -> {
          var now = OffsetDateTime.now();
          if (eventMessageRepository.markSent(eventMessage.getId(), Constant.SYSTEM_USER, now) == 0) {
            log.debug("event message: {} was marked sent", eventMessage.getId());
          }
          return null;
        });
  }


  @Override
  public void markSent(Long id, String updateBy, OffsetDateTime updatedAt) {
    try {
      eventMessageRepository.markSent(id, updateBy, updatedAt);
    } catch (Exception ex) {
      log.error("Mark sent event with id {} fail", id);
    }
  }

  private CompletableFuture<SendResult<String, byte[]>> sendAsync(EventMessage eventMessage) {
    return eventMessage.getPartition() == null
        ? sendAsync(eventMessage.getTopic(), eventMessage.getKey(), eventMessage.getMessageBytes())
        : sendAsync(eventMessage.getTopic(), eventMessage.getKey(), eventMessage.getMessageBytes(), eventMessage.getPartition());
  }


  private int partition(String topic) {
    return kafkaTemplate.partitionsFor(topic).size();
  }

  @Override
  public <T> CompletableFuture<EventMessage> sendAsync(String topic, String key, String username, T message, JsonWriter.WriteObject<T> messageWriter) {
    return sendAsync(topic, key, username, Json.encode(message, messageWriter));
  }

  @Override
  public CompletableFuture<EventMessage> sendAsync(String topic, String key, String username, byte[] message) {
    var savedEvent = save(topic, key, username, message);
    return sendAsync(topic, key, message)
        .orTimeout(30_000, TimeUnit.MILLISECONDS)
        .handle((result, error) -> {
          if (error != null) {
            throw new NSTCompletionException(new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVER_ERROR, String.format("Send message fail to topic %s with key %s", topic, key)));
          }
          return savedEvent;
        });
  }

  private CompletableFuture<SendResult<String, byte[]>> send9093Async(EventMessage eventMessage) {
    return eventMessage.getPartition() == null
            ? send9093Async(eventMessage.getTopic(), eventMessage.getKey(), eventMessage.getMessageBytes())
            : send9093Async(eventMessage.getTopic(), eventMessage.getKey(), eventMessage.getMessageBytes(), eventMessage.getPartition());
  }
  @Override
  public CompletableFuture<Void> sendAndUpdate9093Async(EventMessage eventMessage) {
    return send9093Async(eventMessage)
            .thenApply(res -> {
              var now = OffsetDateTime.now();
              if (eventMessageRepository.markSent(eventMessage.getId(), Constant.SYSTEM_USER, now) == 0) {
                log.debug("event message: {} was marked sent", eventMessage.getId());
              }
              return null;
            });
  }

  @Override
  public CompletableFuture<SendResult<String, byte[]>> send9093Async(String topic, String key, byte[] message) {
    var context = ThreadContext.getImmutableContext();
    return kafka9093Template.send(topic, key, message)
            .whenComplete((v, e) -> {
              ThreadContext.clearMap();
              ThreadContext.putAll(context);
            });
  }

  @Override
  public CompletableFuture<SendResult<String, byte[]>> send9093Async(String topic, String key, byte[] message, Integer partition) {
    var context = ThreadContext.getImmutableContext();
    return kafka9093Template.send(topic, partition, key, message)
            .whenComplete((v, e) -> {
              ThreadContext.clearMap();
              ThreadContext.putAll(context);
            });
  }
}
