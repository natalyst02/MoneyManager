package vn.com.mbbank.adminportal.core.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, String> {
    List<FileEntity> findByUserName(String username, PageRequest of);

    long countByUserName(String username);
}
