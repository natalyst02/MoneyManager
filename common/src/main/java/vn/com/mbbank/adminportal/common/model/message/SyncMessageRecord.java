package vn.com.mbbank.adminportal.common.model.message;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.common.model.OpType;
import vn.com.mbbank.adminportal.common.util.OperationTimestampConverter;

import java.time.OffsetDateTime;

@CompiledJson
@Data
@Accessors(chain = true)
public class SyncMessageRecord<T> {
  private String table;
  @JsonProperty("op_type")
  private OpType opType;
  @JsonAttribute(name = "op_ts", converter = OperationTimestampConverter.class)
  private OffsetDateTime opTs;
  private String pos;
  private T after;
  private T before;
}