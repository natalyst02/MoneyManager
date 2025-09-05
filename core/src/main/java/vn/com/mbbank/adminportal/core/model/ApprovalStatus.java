package vn.com.mbbank.adminportal.core.model;

import org.springframework.data.util.Pair;

import java.util.Set;

public enum ApprovalStatus {
  WAITING_APPROVAL,
  REJECTED,
  APPROVED;

  private static final Set<Pair<ApprovalStatus, ApprovalStatus>> allowedStatusChanges = Set.of(
      Pair.of(WAITING_APPROVAL, APPROVED),
      Pair.of(WAITING_APPROVAL, REJECTED),
      Pair.of(APPROVED, WAITING_APPROVAL),
      Pair.of(REJECTED, WAITING_APPROVAL)
  );

  public boolean isAllowedChange(ApprovalStatus status) {
    return allowedStatusChanges.contains(Pair.of(this, status));
  }
}
