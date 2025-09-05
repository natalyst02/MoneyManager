package vn.com.mbbank.adminportal.common.util;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataHolder<T> {
  private T value;
}
