package vn.com.mbbank.adminportal.core.service;

import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.request.ShareFileRequest;
import vn.com.mbbank.adminportal.core.model.response.FileResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface FileService {
    CompletableFuture<FileResponse> getUserFile(Authentication authentication, Integer page, Integer pageSize);

    CompletableFuture<FileEntity> getFile(String id);

    CompletableFuture<Void> shareFile(ShareFileRequest shareFileRequest
                                      );

    CompletableFuture<FileResponse> getShareUserFile(Authentication authentication, Integer page, Integer pageSize);

    CompletableFuture<FileEntity> getFile(String id, String userName);
}
