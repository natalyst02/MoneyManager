package vn.com.mbbank.adminportal.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.entity.FileLog;
import vn.com.mbbank.adminportal.core.model.entity.FileShareEntity;
import vn.com.mbbank.adminportal.core.model.request.ShareFileRequest;
import vn.com.mbbank.adminportal.core.model.response.FileResponse;
import vn.com.mbbank.adminportal.core.repository.FileLogRepository;
import vn.com.mbbank.adminportal.core.repository.FileRepository;
import vn.com.mbbank.adminportal.core.repository.FileShareRepository;
import vn.com.mbbank.adminportal.core.service.FileService;
import vn.com.mbbank.adminportal.core.util.Authentications;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileShareRepository fileShareRepository;
    private final FileLogRepository fileLogRepository;



    @Override
    public CompletableFuture<FileResponse> getUserFile(Authentication authentication, Integer page, Integer pageSize) {
        var papUser = Authentications.requirePapUser();
        var files = fileRepository.findByUserNameOrderByUpdatedAt(papUser.getUsername(), PageRequest.of(page, pageSize));
        var total = fileRepository.countByUserName(papUser.getUsername());
        return CompletableFuture.completedFuture(new FileResponse().setFiles(files).setTotal(total).setPage(page).setPageSize(pageSize));
    }

    @Override
    public CompletableFuture<FileEntity> getFile(String id) {
        var papUser = Authentications.requirePapUser();
        return CompletableFuture.completedFuture(fileRepository.findByIdAndUserName(id, papUser.getUsername()));
    }

    @Override
    public CompletableFuture<Void> shareFile(ShareFileRequest shareFileRequest) {
        var papUser = Authentications.requirePapUser();

        if (shareFileRequest.getFileIds() == null || shareFileRequest.getFileIds().isEmpty()) {
            throw new PaymentPlatformException(new BusinessErrorCode(CommonErrorCode.INTERNAL_SERVER_ERROR.code(),  "Dữ liệu file không hợp lệ", HttpStatus.INTERNAL_SERVER_ERROR), null);

        }

        List<FileEntity> fileEntities = fileRepository.findByIdInAndUserName(shareFileRequest.getFileIds(), papUser.getUsername());

        if (fileEntities == null ||
        fileEntities.size() != shareFileRequest.getFileIds().size()) {
            FileLog fileLog = new FileLog(
                    UUID.randomUUID().toString(),
                    new Date(),
                    papUser.getUsername(),
                    "SHARE_FILE",
                    shareFileRequest.getFileIds().toString()
            );

            fileLogRepository.save(fileLog);
            throw new PaymentPlatformException(new BusinessErrorCode(CommonErrorCode.INTERNAL_SERVER_ERROR.code(),  "Bạn không có quyền truy cập vào file", HttpStatus.INTERNAL_SERVER_ERROR), null);

        }

        List<FileShareEntity> fileShareEntities = new ArrayList<>();
        for (String fileId : shareFileRequest.getFileIds()) {
            fileShareEntities.add(new FileShareEntity(
                    UUID.randomUUID().toString(),
                    fileId,
                    shareFileRequest.getUsername(),
                    true,
                    new Date(),
                    new Date()
            ));
        }
        fileShareRepository.saveAll(fileShareEntities);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<FileResponse> getShareUserFile(Authentication authentication, Integer page, Integer pageSize) {
        var papUser = Authentications.requirePapUser();
        var fileShare = fileShareRepository.findByUsernameOrderByCreateDate(papUser.getUsername(), PageRequest.of(page, pageSize));
        var total = fileShareRepository.countByUsername(papUser.getUsername());
        Set<String> ids = fileShare.stream().map(FileShareEntity::getFileId).collect(Collectors.toSet());
        List<FileEntity> fileEntities = fileRepository.findByIdIn(ids);
        return CompletableFuture.completedFuture(new FileResponse().setFiles(fileEntities).setTotal(total).setPage(page).setPageSize(pageSize));
    }

    @Override
    public CompletableFuture<FileEntity> getFile(String id, String userName) {
        var fileShare = fileShareRepository.findByUsernameAndFileId(userName, id);

        if (fileShare == null) {
            FileLog fileLog = new FileLog(
                    UUID.randomUUID().toString(),
                    new Date(),
                    userName,
                    "DOWNLOAD_FILE",
                    id
            );

            fileLogRepository.save(fileLog);
            throw new PaymentPlatformException(new BusinessErrorCode(CommonErrorCode.INTERNAL_SERVER_ERROR.code(),  "Bạn không có quyền truy cập vào file", HttpStatus.INTERNAL_SERVER_ERROR), null);
        }
        return CompletableFuture.completedFuture(fileRepository.findById(id).get());
    }
}
