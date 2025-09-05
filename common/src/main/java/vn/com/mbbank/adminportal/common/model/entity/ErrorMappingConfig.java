package vn.com.mbbank.adminportal.common.model.entity;

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
@Table(name = "PAP_ERROR_MAPPING_CONFIG")
@Entity
public class ErrorMappingConfig {
  @Id
  @SequenceGenerator(name = "PAP_ERROR_MAPPING_CONFIG_ID_SEQ", sequenceName = "PAP_ERROR_MAPPING_CONFIG_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_ERROR_MAPPING_CONFIG_ID_SEQ")
  Long id;
  String code;
  String description;
  String descriptionVi;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
