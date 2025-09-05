package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.PermissionType;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_PERMISSION_CATEGORY")
@Entity
public class PermissionCategory {
  @Id
  @SequenceGenerator(name = "PAP_PERMISSION_CATEGORY_ID_SEQ", sequenceName = "PAP_PERMISSION_CATEGORY_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_PERMISSION_CATEGORY_ID_SEQ")
  Long id;
  Long permissionId;
  @Enumerated(EnumType.STRING)
  PermissionType type;
  String subType;
  OffsetDateTime createdAt;
}
