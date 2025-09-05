package vn.com.mbbank.adminportal.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PriorityReference {
  private Long id;
  private Integer priority;
}