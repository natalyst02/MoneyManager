package vn.com.mbbank.adminportal.core.model;


import lombok.Getter;

import java.util.List;

@Getter
public enum TransferChannel {
  INHOUSE(TransferType.FAST),
  ACH(TransferType.FAST),
  BILATERAL(TransferType.FAST),
  IBFT(TransferType.FAST),
  NAPAS2(TransferType.FAST),
  CITAD(TransferType.IBPS),
  BIDV(TransferType.IBPS),
  VCB(TransferType.IBPS);

  public static final List<TransferChannel> ALL_TRANSFER_CHANNELS = List.of(TransferChannel.values());

  private final TransferType transferType;

  TransferChannel(TransferType transferType) {
    this.transferType = transferType;
  }

  public static List<TransferChannel> getByTransferType(TransferType transferType) {
    return ALL_TRANSFER_CHANNELS.stream()
        .filter(channel -> channel.getTransferType() == transferType)
        .toList();
  }
  public static List<TransferChannel> getAllowedTransferChannelLimits() {
    return List.of(ACH, IBFT, NAPAS2);
  }
}
