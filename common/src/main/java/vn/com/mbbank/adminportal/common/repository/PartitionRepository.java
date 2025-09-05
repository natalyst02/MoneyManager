package vn.com.mbbank.adminportal.common.repository;

public interface PartitionRepository<T> {
  void dropPartitions(int olderMonths);
}
