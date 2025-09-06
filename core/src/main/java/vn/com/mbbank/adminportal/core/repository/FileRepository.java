package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.mbbank.adminportal.core.model.FileStatus;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FileRepository extends JpaRepository<FileEntity, String> {
    List<FileEntity> findByUserNameOrderByUpdatedAt(String username, PageRequest of);

    long countByUserName(String username);

    FileEntity findByIdAndUserName(String id, String username);

    List<FileEntity> findByIdInAndUserName(Set<String> id, String username);

    List<FileEntity> findByIdIn(Set<String> id);

    Optional<FileEntity> findById(String id);


    List<FileEntity> findByStatus(String status, PageRequest of);
}
