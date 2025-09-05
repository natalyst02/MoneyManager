package vn.com.mbbank.adminportal.common.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@CompiledJson
@Data
@Accessors(chain = true)
public class SyncMessage<T> {
  private OpType action;
  private OffsetDateTime iat;
  private String clientMessageId;
  private String target;
  private T data;
}
