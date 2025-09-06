package vn.com.mbbank.adminportal.core.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.entity.FileShareEntity;

import java.util.List;

public interface FileShareRepository extends JpaRepository<FileShareEntity, String> {
    List<FileShareEntity> findByUserNameOrderByCreateDate(String username, PageRequest of);

    long countByUserName(String username);

    FileShareEntity findByUsernameAndFileId(String username, String fileId);

}
