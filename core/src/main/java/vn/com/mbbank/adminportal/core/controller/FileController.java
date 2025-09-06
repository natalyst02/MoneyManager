package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.response.FileResponse;
import vn.com.mbbank.adminportal.core.service.FileService;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.model.GetHcmUserInfoResponse;

import java.util.concurrent.CompletableFuture;

@Log4j2
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Validated
public class FileController {
    private final FileService fileService;

    @GetMapping("")
    public CompletableFuture<Response<FileResponse>> getFileByUser(Authentication authentication,
                                                                   @RequestParam(required = false, defaultValue = "1") Integer page,
                                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return fileService.getUserFile(authentication, page, pageSize).thenApply(Response::ofSucceeded);
    }
}
