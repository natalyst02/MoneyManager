package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_BANK")
@Entity
public class Bank {
  @Id
  @SequenceGenerator(name = "PAP_BANK_ID_SEQ", sequenceName = "PAP_BANK_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_BANK_ID_SEQ")
  Long id;
  String code;
  String ibftCode;
  String shortName;
  String fullName;
  String reason;
  Boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
