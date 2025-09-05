package vn.com.mbbank.adminportal.common.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@CompiledJson
public class PapUser implements User {
  private Long userId;
  private String username;
  private List<Long> roleIds;
  private Map<String, Integer> permissions;
  private String sourceAppId;
  private Long tokenExpiredAt;
}
