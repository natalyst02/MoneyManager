package vn.com.mbbank.adminportal.core.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.entity.Status;
import vn.com.mbbank.adminportal.core.model.response.FileResponse;
import vn.com.mbbank.adminportal.core.repository.FileRepository;
import vn.com.mbbank.adminportal.core.util.Authentications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {
    private final String uploadDir;
    private final FileRepository fileRepository;


    public FileUploadService(@Value("${file.upload-dir:uploads}") String uploadDir, FileRepository fileRepository) {
        this.uploadDir = uploadDir;
        this.fileRepository = fileRepository;
    }


    public FileEntity uploadFile(Authentication authentication, MultipartFile file) throws IOException {
        var papUser = Authentications.requirePapUser();
        // Tạo thư mục uploads nếu chưa tồn tại
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file unique để tránh trùng lặp
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        if (!StringUtils.equals(fileExtension, ".pdf") && !StringUtils.equals(fileExtension, ".docx")){
            throw new PaymentPlatformException(new BusinessErrorCode(CommonErrorCode.INTERNAL_SERVER_ERROR.code(),  "fileExtension không hợp lệ", HttpStatus.INTERNAL_SERVER_ERROR), null);
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Đường dẫn lưu file
        Path filePath = uploadPath.resolve(uniqueFileName);

        // Lưu file vào thư mục
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Tạo FileInfo entity và lưu vào database
        FileEntity fileInfo = new FileEntity(
                UUID.randomUUID().toString(),
                originalFileName,
                filePath.toString(),
                fileExtension,
                Status.INIT.toString(),
                null,
                papUser.getUsername(),
                new Date(),
                new Date()
        );

        return fileRepository.save(fileInfo);
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

}
