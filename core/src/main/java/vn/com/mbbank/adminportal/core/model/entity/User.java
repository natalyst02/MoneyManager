package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_USER")
@Entity
public class User {
  @Id
  @SequenceGenerator(name = "PAP_USER_ID_SEQ", sequenceName = "PAP_USER_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_USER_ID_SEQ")
  Long id;
  String username;
  String keycloakId;
  String employeeCode;
  String fullName;
  String phoneNumber;
  String email;
  String jobName;
  String orgName;
  String reason;
  boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
  String password;
}