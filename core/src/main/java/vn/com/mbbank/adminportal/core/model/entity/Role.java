package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.Convert;
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
import vn.com.mbbank.adminportal.core.model.RoleType;
import vn.com.mbbank.adminportal.core.util.converter.RolePermissionConverter;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
@Table(name = "PAP_ROLE")
@Entity
public class Role {
  @Id
  @SequenceGenerator(name = "PAP_ROLE_ID_SEQ", sequenceName = "PAP_ROLE_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_ROLE_ID_SEQ")
  Long id;
  String code;
  String name;
  @Enumerated(EnumType.STRING)
  RoleType type;
  String description;
  @Convert(converter = RolePermissionConverter.class)
  Map<String, Integer> permissions;
  String reason;
  boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
