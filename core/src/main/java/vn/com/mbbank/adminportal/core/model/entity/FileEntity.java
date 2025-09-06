package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "FILE_ENTITY")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity {
    @Id
    private String id;
    private String fileName;
    private String fileUrl;
    private String fileExtension;
    private String status;
    private String fileInfo;
    private String userName;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    private String contentType;
    private String textDetect;

}
