package vn.com.mbbank.adminportal.common.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.common.model.ErrorMessageStatus;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "PAP_ERROR_MESSAGE")
public class ErrorMessage {
  @Id Long id;
  String topic;
  int partition;
  long offset;
  String key;
  String message;
  String error;
  @Enumerated(EnumType.STRING)
  ErrorMessageStatus status;
  String createdBy;
  OffsetDateTime createdAt;
  String updatedBy;
  OffsetDateTime updatedAt;
}
