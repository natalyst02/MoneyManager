package vn.com.mbbank.adminportal.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.core.model.FileStatus;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.request.FileInput;
import vn.com.mbbank.adminportal.core.model.response.FileResponse;
import vn.com.mbbank.adminportal.core.repository.FileRepository;
import vn.com.mbbank.adminportal.core.repository.impl.CustomizeFileRepoImpl;
import vn.com.mbbank.adminportal.core.service.FileService;
import vn.com.mbbank.adminportal.core.util.Authentications;
import vn.com.mbbank.adminportal.core.validator.FileInputValidator;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileInputValidator fileInputValidator;
    private final CustomizeFileRepoImpl customizeFileRepoImpl;


    @Override
    public CompletableFuture<FileResponse> getUserFile(Authentication authentication, FileInput fileInput) {
        fileInputValidator.validate(fileInput);
        var papUser = Authentications.requirePapUser();
        var files = customizeFileRepoImpl.getUserFile(fileInput, papUser.getUsername(), PageRequest.of(fileInput.getPage() - 1, fileInput.getPageSize()));
        var total = customizeFileRepoImpl.countUserFile(fileInput, papUser.getUsername());
        return CompletableFuture.completedFuture(new FileResponse().setFiles(files).setTotal(total).setPage(fileInput.getPage()).setPageSize(fileInput.getPageSize()));
    }


    @Override
    public CompletableFuture<FileResponse> getAllUserFile(Authentication authentication, Integer page, Integer pageSize) {
        var papUser = Authentications.requirePapUser();
        var files = fileRepository.findByUserName(papUser.getUsername(), PageRequest.of(page, pageSize));
        var total = fileRepository.countByUserName(papUser.getUsername());
        return CompletableFuture.completedFuture(new FileResponse().setFiles(files).setTotal(total).setPage(page).setPageSize(pageSize));
    }

    @Override
    public List<FileEntity> getFilesByStatus(int batchSize, FileStatus status) {
        return fileRepository.findByStatus(status.name(), PageRequest.of(0, batchSize));
    }

    @Override
    public void save(FileEntity file) {
        fileRepository.saveAndFlush(file);
    }

    @Override
    public CompletableFuture<FileEntity> getFile(String id) {
        var papUser = Authentications.requirePapUser();
        return CompletableFuture.completedFuture(fileRepository.findByIdAndUserName(id, papUser.getUsername()));
    }
}
