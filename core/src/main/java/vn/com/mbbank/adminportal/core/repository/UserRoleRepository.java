package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import vn.com.mbbank.adminportal.core.model.RoleInfo;
import vn.com.mbbank.adminportal.core.model.entity.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends BaseJpaRepository<UserRole, Long>, JpaSpecificationExecutor<UserRole>,CustomizedUserRoleRepository {
    @Query("SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId")
    List<Long> findRoleIdsByUserId(Long userId);

    @Query(value = "select ur from UserRole ur where ur.userId = :id")
    List<UserRole> getUserRoleByUserId(Long id);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId AND ur.roleId IN :roleIds")
    void deleteByUserIdAndRoleIds(Long userId, List<Long> roleIds);

    @Query("SELECT new vn.com.mbbank.adminportal.core.model.RoleInfo(r.id, r.code,r.name,r.type) " +
        "FROM UserRole ur JOIN Role r ON ur.roleId = r.id " +
        "WHERE ur.userId = :userId")
    List<RoleInfo> findRolesByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId")
    void deleteByUserId(Long userId);
}
