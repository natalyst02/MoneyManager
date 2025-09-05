package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo;
import vn.com.mbbank.adminportal.core.model.PermissionType;
import vn.com.mbbank.adminportal.core.model.entity.Permission;

import java.util.List;

public interface PermissionRepository extends BaseJpaRepository<Permission, Long> {
  @Query(value = "select distinct p.module from Permission p")
  List<String> getAllModules();

  @Query(value = """
    select new vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo(p.id, pc.type, pc.subType, p.module, p.action, 
    p.bitmaskValue, p.description, p.createdAt) 
    from Permission p join PermissionCategory pc on p.id = pc.permissionId order by pc.id
""")
  List<PermissionCategoryInfo> getAllPermission();

  @Query(value = """
    select new vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo(p.id, pc.type, pc.subType, p.module, p.action, 
    p.bitmaskValue, p.description, p.createdAt) 
    from Permission p join PermissionCategory pc on p.id = pc.permissionId where pc.type = :type order by pc.id
""")
  List<PermissionCategoryInfo> getPermissionsByType(PermissionType type);

  @Query(value = """
    select new vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo(p.id, pc.type, pc.subType, p.module, p.action, 
    p.bitmaskValue, p.description, p.createdAt) 
    from Permission p join PermissionCategory pc on p.id = pc.permissionId where pc.type = :type and p.id in :ids order by pc.id
""")
  List<PermissionCategoryInfo> getPermissionsByTypeAndIdIn(PermissionType type, List<Long> ids);
}
