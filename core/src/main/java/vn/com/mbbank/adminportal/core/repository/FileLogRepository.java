package vn.com.mbbank.adminportal.core.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.mbbank.adminportal.core.model.entity.FileLog;
import vn.com.mbbank.adminportal.core.model.entity.FileShareEntity;

import java.util.List;

public interface FileLogRepository extends JpaRepository<FileLog, String> {

}
