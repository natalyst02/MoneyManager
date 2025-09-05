package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_ERROR_CODE_CONFIG")
@Entity
public class ErrorCodeConfig {
  @Id
  @SequenceGenerator(name = "PAP_ERROR_CODE_CONFIG_ID_SEQ", sequenceName = "PAP_ERROR_CODE_CONFIG_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_ERROR_CODE_CONFIG_ID_SEQ")
  Long id;
  String originalService;
  String errorCode;
  String errorDesc;
  Boolean type;
  @Enumerated(EnumType.STRING)
  TransferChannel transferChannel;
  Boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
