package vn.com.mbbank.adminportal.core.model;

public interface TransferChannelReference {
  Long getId();
  TransferChannel getTransferChannel();
  Integer getPriority();
}
