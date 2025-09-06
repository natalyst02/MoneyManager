package vn.com.mbbank.adminportal.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.response.FileResponse;
import vn.com.mbbank.adminportal.core.repository.FileRepository;
import vn.com.mbbank.adminportal.core.service.FileService;
import vn.com.mbbank.adminportal.core.util.Authentications;

import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;


    @Override
    public CompletableFuture<FileResponse> getUserFile(Authentication authentication, Integer page, Integer pageSize) {
        var papUser = Authentications.requirePapUser();
        var files = fileRepository.findByUserName(papUser.getUsername(), PageRequest.of(page, pageSize));
        var total = fileRepository.countByUserName(papUser.getUsername());
        return CompletableFuture.completedFuture(new FileResponse().setFiles(files).setTotal(total).setPage(page).setPageSize(pageSize));
    }
}
