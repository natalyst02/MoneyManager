package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "FILE_SHARE")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FileShareEntity {

    @Id
    private String id;
    private String fileId;
    private String username;
    private Boolean active;
    @CreationTimestamp
    private Date createDate;
    @UpdateTimestamp
    private Date updateDate;

}
