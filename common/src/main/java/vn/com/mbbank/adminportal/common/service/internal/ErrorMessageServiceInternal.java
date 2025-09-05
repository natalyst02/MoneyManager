package vn.com.mbbank.adminportal.common.service.internal;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import vn.com.mbbank.adminportal.common.model.entity.ErrorMessage;
import vn.com.mbbank.adminportal.common.service.ErrorMessageService;
import vn.com.mbbank.adminportal.common.util.ErrorMessageProcessor;

import java.util.List;

public interface ErrorMessageServiceInternal extends ErrorMessageService {

  default ErrorMessage save(String topic, int partition, long offset, String message, Throwable cause) {
    return save(topic, partition, offset, null, message, cause);
  }

  ErrorMessage save(String topic, int partition, long offset, String key, String message, Throwable cause);

  List<ErrorMessage> save(ConsumerRecords<String, byte[]> records, Throwable cause);

  List<ErrorMessage> save(ConsumerRecords<String, byte[]> records, List<Throwable> causes);

  List<ErrorMessage> save(List<ConsumerRecord<String, byte[]>> records, List<Throwable> causes);

  ErrorMessage getErrorMessage0(Long id);

  ErrorMessageServiceInternal addProcessor(ErrorMessageProcessor processor);

  void dropPartitions(Integer olderMonths);
}
