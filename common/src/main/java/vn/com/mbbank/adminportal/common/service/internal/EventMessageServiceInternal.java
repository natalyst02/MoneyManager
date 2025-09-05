package vn.com.mbbank.adminportal.common.service.internal;

import com.dslplatform.json.JsonWriter;
import org.springframework.kafka.support.SendResult;
import vn.com.mbbank.adminportal.common.model.entity.EventMessage;
import vn.com.mbbank.adminportal.common.model.request.CreateEventMessageRequest;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface EventMessageServiceInternal {
  EventMessage save(String topic, String key, byte[] message);

  /**
   * save event message into db with new topic with partition
   *
   * @param topic
   * @param key
   * @param message
   * @param partition
   * @return
   */
  EventMessage save(String topic, String key, byte[] message, Integer partition);

  /**
   * save event message into db
   *
   * @param topic
   * @param key
   * @param message
   * @return
   */
  EventMessage save(String topic, String key, String message);

  /**
   * save event message into db with new topic
   *
   * @param topic
   * @param key
   * @param userName
   * @param message
   * @return
   */
  EventMessage save(String topic, String key, String userName, byte[] message);

  /**
   * save event message into db with new topic
   *
   * @param topic
   * @param eventMessage
   * @return
   */
  EventMessage save(String topic, EventMessage eventMessage);

  /**
   * save event message into db with new topic with partition
   *
   * @param topic
   * @param key
   * @param message
   * @param partition
   * @return
   */
  EventMessage save(String topic, String key, String message, Integer partition);

  List<EventMessage> save(List<CreateEventMessageRequest> requests);

  Stream<EventMessage> getUnsentEventMessages(String topic);

  Stream<EventMessage> getUnsentEventMessages(String topic, OffsetDateTime createdAtTo);
  Stream<EventMessage> getUnsentEventMessages(List<String> topics, OffsetDateTime createdAtTo);

  /**
   * send event message to message queue
   *
   * @param topic
   * @param key
   * @param message
   */
  CompletableFuture<SendResult<String, byte[]>> sendAsync(String topic, String key, byte[] message);

  CompletableFuture<SendResult<String, byte[]>> sendAsync(String topic, String key, byte[] message, Integer partition);

  /**
   * see {@link EventMessageServiceInternal#sendAsync(String, String, byte[])}
   *
   * @param topic
   * @param key
   * @param message
   */
  default CompletableFuture<SendResult<String, byte[]>> sendAsync(String topic, String key, String message) {
    return sendAsync(topic, key, message.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * send all unsent message in a given <i>topic</i>
   *
   * @param topic
   */

  void send(String topic);

  void send(String topic, OffsetDateTime createAtTo);

  void send(List<String> topics, OffsetDateTime createAtTo);

  CompletableFuture<Void> sendAsync(String topic);

  CompletableFuture<Void> sendAsync(String topic, OffsetDateTime createAtTo);

  CompletableFuture<Void> sendAsync(List<String> topics, OffsetDateTime createAtTo);

  /**
   * send a given <i>eventMessage</i> to message queue. Update event message status if sent success.
   *
   * @param eventMessage
   */
  CompletableFuture<Void> sendAndUpdateAsync(EventMessage eventMessage);

  default List<CompletableFuture<Void>> sendAndUpdateAsync(List<EventMessage> eventMessages) {
    return eventMessages.stream().map(this::sendAndUpdateAsync).toList();
  }

  void markSent(Long id, String updateBy, OffsetDateTime updatedAt);

  CompletableFuture<EventMessage> sendAsync(String topic, String key, String username, byte[] message);

  <T> CompletableFuture<EventMessage> sendAsync(String topic, String key, String username, T message, JsonWriter.WriteObject<T> messageWriter);

  CompletableFuture<Void> sendAndUpdate9093Async(EventMessage eventMessage);
  CompletableFuture<SendResult<String, byte[]>> send9093Async(String topic, String key, byte[] message);
  CompletableFuture<SendResult<String, byte[]>> send9093Async(String topic, String key, byte[] message, Integer partition);
}
