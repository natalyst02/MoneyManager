package vn.com.mbbank.adminportal.core.service;

import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.core.model.FileStatus;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.request.FileInput;
import vn.com.mbbank.adminportal.core.model.response.FileResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FileService {

    CompletableFuture<FileEntity> getFile(String id);
    CompletableFuture<FileResponse> getUserFile(Authentication authentication, FileInput fileInput);
    CompletableFuture<FileResponse> getAllUserFile(Authentication authentication, Integer page, Integer pageSize);
    List<FileEntity> getFilesByStatus(int batchSize,  FileStatus status);

    void save(FileEntity file);
}
