package vn.com.mbbank.adminportal.core.service;

import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.response.FileResponse;

import java.util.concurrent.CompletableFuture;

public interface FileService {
    CompletableFuture<FileResponse> getUserFile(Authentication authentication, Integer page, Integer pageSize);

    CompletableFuture<FileEntity> getFile(String id);
}
