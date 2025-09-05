package vn.com.mbbank.adminportal.common.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.mbbank.adminportal.common.model.ErrorMessageStatus;
import vn.com.mbbank.adminportal.common.model.entity.ErrorMessage;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component("errorMessagePartitionRepository")
public interface ErrorMessageRepository extends BaseJpaRepository<ErrorMessage, Long>, JpaSpecificationExecutor<ErrorMessage>, PartitionRepository<ErrorMessage> {
  @Query(value = "SELECT ERROR_MESSAGE_ID_SEQ.nextval FROM DUAL", nativeQuery = true)
  Long nextErrorMessageId();

  @Query(value = "SELECT ERROR_MESSAGE_ID_SEQ.nextval FROM DUAL CONNECT BY LEVEL <= :num", nativeQuery = true)
  List<Long> nextErrorMessageIds(int num);

  @Query("select m from ErrorMessage m where m.topic = :topic")
  Stream<ErrorMessage> getErrorMessages(String topic);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Transactional
  @Query("select m from ErrorMessage m where m.id = :id and m.status = :status")
  Optional<ErrorMessage> getLockedErrorMessage(Long id, ErrorMessageStatus status);

  @Modifying
  @Transactional
  @Query("update ErrorMessage msg set msg.status = :newStatus, msg.updatedAt = :updatedAt where msg.id = :id and msg.status = :oldStatus")
  int setStatus(Long id, ErrorMessageStatus oldStatus, ErrorMessageStatus newStatus, OffsetDateTime updatedAt);

  @Modifying
  @Transactional
  @Query("update ErrorMessage msg set msg.error = :error, msg.updatedAt =:updatedAt where msg.id = :id and msg.status = 'INIT'")
  int setError(Long id, String error, OffsetDateTime updatedAt);
}
