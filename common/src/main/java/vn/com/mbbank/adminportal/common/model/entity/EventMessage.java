package vn.com.mbbank.adminportal.common.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "PAP_EVENT_MESSAGE")
public class EventMessage {
  @Id
  private Long id;
  private String topic;
  private String key;
  private String message;
  @SuppressWarnings("java:S2065")
  @Setter(AccessLevel.NONE)
  private transient byte[] messageBytes;
  private boolean sent;
  private String createdBy;
  private OffsetDateTime createdAt;
  private String updatedBy;
  private OffsetDateTime updatedAt;
  private Integer partition;

  public EventMessage() {
  }

  public EventMessage(String topic, String key, String message) {
    this.topic = topic;
    this.key = key;
    this.message = message;
  }

  public EventMessage(String topic, String key, byte[] messageBytes) {
    this.topic = topic;
    this.key = key;
    this.message = new String(messageBytes, StandardCharsets.UTF_8);
    this.messageBytes = messageBytes;
  }

  public EventMessage(Long id, String topic, EventMessage other) {
    this.id = id;
    this.topic = topic;
    this.key = other.key;
    this.message = other.message;
    this.messageBytes = other.messageBytes;
    this.createdBy = other.createdBy;
    this.createdAt = other.createdAt;
    this.updatedBy = other.createdBy;
    this.updatedAt = other.createdAt;
    this.partition = other.partition;
  }

  public EventMessage setMessage(String message) {
    this.message = message;
    messageBytes = null;
    return this;
  }

  public byte[] getMessageBytes() {
    if (messageBytes == null && message != null) {
      messageBytes = message.getBytes(StandardCharsets.UTF_8);
    }
    return messageBytes;
  }
}