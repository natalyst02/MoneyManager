package vn.com.mbbank.adminportal.common.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateEventMessageRequest {
  private String topic;
  private String key;
  private byte[] message;
  private Integer partition;
}
