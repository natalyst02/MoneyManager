package vn.com.mbbank.adminportal.core.model.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

@Getter
@Setter
@Accessors(chain = true)
public class TransferChannelBankConfigFilter {
  private String bankCode;
  private String cardBin;
  private TransferChannel transferChannel;
  private Boolean active;
  private int page = 0;
  private int size = 10;
  private String sort = "id:ASC";
}
