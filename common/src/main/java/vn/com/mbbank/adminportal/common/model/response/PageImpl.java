package vn.com.mbbank.adminportal.common.model.response;

import com.dslplatform.json.CompiledJson;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@CompiledJson
public class PageImpl<T> {
  Integer page;
  Integer size;
  Long total;
  List<T> content;

  public PageImpl() {
  }

  @CompiledJson
  public PageImpl(Integer page, Integer size, Long total, List<T> content) {
    this.page = page;
    this.size = size;
    this.total = total;
    this.content = content;
  }
}
