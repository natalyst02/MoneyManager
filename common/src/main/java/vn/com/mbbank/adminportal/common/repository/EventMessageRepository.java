package vn.com.mbbank.adminportal.common.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.mbbank.adminportal.common.model.entity.EventMessage;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component("eventMessagePartitionRepository")
public interface EventMessageRepository extends BaseJpaRepository<EventMessage, Long> {
  @Query(value = "SELECT PAP_EVENT_MESSAGE_ID_SEQ.nextval FROM DUAL", nativeQuery = true)
  Long nextEventMessageId();

  @Query(value = "SELECT PAP_EVENT_MESSAGE_ID_SEQ.nextval FROM DUAL CONNECT BY LEVEL <= :num", nativeQuery = true)
  List<Long> nextEventMessageIds(int num);

  @Query("select m from EventMessage m where m.topic = :topic and m.sent = false order by id")
  Stream<EventMessage> getUnsentEventMessages(String topic);

  @Query("select m from EventMessage m where m.topic = :topic and m.sent = false and createdAt < :createdAtTo order by id")
  Stream<EventMessage> getUnsentEventMessages(String topic, OffsetDateTime createdAtTo);

  @Query("select m from EventMessage m where m.topic in :topics and m.sent = false and createdAt < :createdAtTo order by id")
  Stream<EventMessage> getUnsentEventMessages(List<String> topics, OffsetDateTime createdAtTo);

  @Transactional
  @Modifying
  @Query("update EventMessage set sent = true, updatedBy = :updatedBy, updatedAt = :updatedAt where id = :id and sent = false")
  int markSent(Long id, String updatedBy, OffsetDateTime updatedAt);

  List<EventMessage> findByTopic(String topic);
}
