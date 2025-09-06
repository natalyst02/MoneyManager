package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.response.FileResponse;
import vn.com.mbbank.adminportal.core.service.FileService;
import vn.com.mbbank.adminportal.core.service.FileUploadService;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.model.GetHcmUserInfoResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Log4j2
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Validated
public class FileController {
    private final FileService fileService;
    private final FileUploadService fileUploadService;

    @GetMapping("")
    public CompletableFuture<Response<FileResponse>> getFileByUser(Authentication authentication,
                                                                   @RequestParam(required = false, defaultValue = "1") Integer page,
                                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return fileService.getUserFile(authentication, page, pageSize).thenApply(Response::ofSucceeded);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasPermission('user', T(vn.com.mbbank.adminportal.core.model.BitmaskValue).VIEW)")
    public Response<FileEntity> uploadFile(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            // Kiểm tra file có rỗng không
            if (file.isEmpty()) {
                throw new PaymentPlatformException(new BusinessErrorCode(CommonErrorCode.INTERNAL_SERVER_ERROR.code(),  "File không được để trống", HttpStatus.INTERNAL_SERVER_ERROR), null);

            }

            // Kiểm tra kích thước file (giới hạn 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new PaymentPlatformException(new BusinessErrorCode(CommonErrorCode.INTERNAL_SERVER_ERROR.code(),  "File không được vượt quá 10MB", HttpStatus.INTERNAL_SERVER_ERROR), null);
            }

            FileEntity fileInfo = fileUploadService.uploadFile(authentication, file);
            return Response.ofSucceeded(fileInfo);

        } catch (IOException e) {
            throw new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVER_ERROR,  "Lỗi upload file");
        }
    }


}
