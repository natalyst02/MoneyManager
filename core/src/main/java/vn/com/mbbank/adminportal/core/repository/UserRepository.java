package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import vn.com.mbbank.adminportal.core.model.entity.User;
import vn.com.mbbank.adminportal.core.model.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseJpaRepository<User, Long>, CustomizedUserRepository {
  Optional<User> findById(Long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Transactional
  @Query(value = "select u from User u where u.id = :id")
  Optional<User> getLockedUserById(Long id);

  Optional<User> findByUsernameAndActive(String username, boolean active);

  @Query("SELECT new vn.com.mbbank.adminportal.core.model.response.UserResponse(" +
      "u.id, " +
      "u.username, " +
      "u.employeeCode, " +
      "u.fullName, " +
      "u.phoneNumber, " +
      "u.email, " +
      "u.jobName, " +
      "u.orgName, " +
      "CASE WHEN COUNT(r.id) = 0 THEN NULL ELSE '[' || LISTAGG('{\"roleId\": ' || r.id || ', \"roleCode\": \"' || r.code || '\", \"roleName\": \"' || r.name || '\", \"roleType\": \"' || r.type || '\"}', ',') " +
      "WITHIN GROUP (ORDER BY r.id) || ']' " +
      "END, " +
      "u.reason, " +
      "u.active, " +
      "u.createdAt, " +
      "u.createdBy, " +
      "u.updatedAt, " +
      "u.updatedBy) " +
      "FROM User u " +
      "LEFT JOIN UserRole ur ON u.id = ur.userId " +
      "LEFT JOIN Role r ON ur.roleId = r.id " +
      "WHERE (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) " +
      "AND (:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
      "AND (:phoneNumber IS NULL OR u.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')) " +
      "AND (:active IS NULL OR u.active = :active) " +
      "AND (:roleCode IS NULL OR LOWER(r.code) LIKE LOWER(CONCAT('%', :roleCode, '%'))) " +
      "AND (:roleName IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :roleName, '%'))) " +
      "GROUP BY u.id, u.username, u.employeeCode, u.fullName, u.phoneNumber, u.email, " +
      "u.jobName, u.orgName, u.reason, u.active, u.createdAt, u.createdBy, " +
      "u.updatedAt, u.updatedBy ")
  Page<UserResponse> findUsers(
      String username,
      String fullName,
      String phoneNumber,
      Boolean active,
      String roleCode,
      String roleName,
      Pageable pageable
  );

  @Query("SELECT new vn.com.mbbank.adminportal.core.model.response.UserResponse(" +
      "u.id, " +
      "u.username, " +
      "u.employeeCode, " +
      "u.fullName, " +
      "u.phoneNumber, " +
      "u.email, " +
      "u.jobName, " +
      "u.orgName, " +
      "CASE WHEN COUNT(r.id) = 0 THEN NULL ELSE '[' || LISTAGG('{\"roleId\": ' || r.id || ', \"roleCode\": \"' || r.code || '\", \"roleName\": \"' || r.name || '\", \"roleType\": \"' || r.type || '\"}', ',') " +
      "                WITHIN GROUP (ORDER BY r.id) || ']'\n" +
      "       END, " +
      "u.reason, " +
      "u.active, " +
      "u.createdAt, " +
      "u.createdBy, " +
      "u.updatedAt, " +
      "u.updatedBy) " +
      "FROM User u " +
      "LEFT JOIN UserRole ur ON u.id = ur.userId " +
      "LEFT JOIN Role r ON ur.roleId = r.id " +
      "WHERE ( u.id = :id) " +
      "GROUP BY u.id, u.username, u.employeeCode, u.fullName, u.phoneNumber, u.email, " +
      "u.jobName, u.orgName, u.reason, u.active, u.createdAt, u.createdBy, " +
      "u.updatedAt, u.updatedBy ")
  Optional<UserResponse> findUserById(
      Long id
  );

  Optional<User> findByUsername(String username);

  @Query(value = """
        select u.username from User u join UserRole ur on u.id = ur.userId where ur.roleId = :roleId
      """)
  List<String> getUsernamesByRoleId(Long roleId);
}
