package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_PERMISSION")
@Entity
public class Permission {
  @Id
  @SequenceGenerator(name = "PAP_PERMISSION_ID_SEQ", sequenceName = "PAP_PERMISSION_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_PERMISSION_ID_SEQ")
  Long id;
  String module;
  String action;
  String description;
  int bitmaskValue;
  OffsetDateTime createdAt;
}
