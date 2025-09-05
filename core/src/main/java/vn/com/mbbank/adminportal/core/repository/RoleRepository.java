package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.com.mbbank.adminportal.core.model.RolesPermissions;
import vn.com.mbbank.adminportal.core.model.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends BaseJpaRepository<Role, Long>, JpaSpecificationExecutor<Role>, CustomizedRoleRepository {
  Optional<Role> findByIdAndActive(Long id, boolean active);

  Optional<Role> findByCode(String code);

  @Query(value = "select r.id as roleId, r.permissions as permissions from Role r join UserRole ur on r.id = ur.roleId join User u on u.id = ur.userId where ur.userId = :userId and r.active = :roleStatus")
  List<RolesPermissions> getRolesPermissionsByUserId(Long userId, boolean roleStatus);

  @Query("SELECT COUNT(r) FROM Role r WHERE r.id IN :roleIds and r.active = true")
  long countActiveRolesByIdIn(List<Long> roleIds);

  @Query("SELECT r.id FROM Role r WHERE r.id IN :roleIds and r.active = true")
  List<Long> findAllActiveRoleIdsByIds(List<Long> roleIds);
}
