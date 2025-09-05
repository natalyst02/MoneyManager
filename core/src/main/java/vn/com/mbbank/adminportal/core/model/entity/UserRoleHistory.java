package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.Action;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_USER_ROLE_HISTORY")
@Entity
public class UserRoleHistory {
  @Id
  @SequenceGenerator(name = "PAP_USER_ROLE_HISTORY_ID_SEQ", sequenceName = "PAP_USER_ROLE_HISTORY_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_USER_ROLE_HISTORY_ID_SEQ")
  Long id;
  @Enumerated(EnumType.STRING)
  Action action;
  Long userRoleId;
  Long userId;
  Long roleId;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}