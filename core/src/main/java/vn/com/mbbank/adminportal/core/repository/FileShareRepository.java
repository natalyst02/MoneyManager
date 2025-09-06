package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.entity.FileShareEntity;

import java.util.List;

public interface FileShareRepository extends BaseJpaRepository<FileShareEntity, String> {
    List<FileShareEntity> findByUsernameOrderByCreateDate(String username, PageRequest of);

    long countByUsername(String username);

    FileShareEntity findByUsernameAndFileId(String username, String fileId);

}
