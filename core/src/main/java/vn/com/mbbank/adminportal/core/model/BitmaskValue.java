package vn.com.mbbank.adminportal.core.model;

public enum BitmaskValue {
  VIEW(1),
  INSERT(2),
  UPDATE(4),
  DELETE(8),
  ASSIGN(16),
  RETRY(32),
  APPROVE(64),
  REPLY(128);

  private final int bitmask;

  BitmaskValue(int bitmask) {
    this.bitmask = bitmask;
  }

  public int getBitmask() {
    return this.bitmask;
  }
}
