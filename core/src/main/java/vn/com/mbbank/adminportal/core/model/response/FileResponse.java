package vn.com.mbbank.adminportal.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;

import java.util.List;

@Data
@Accessors(chain = true)
public class FileResponse {
    List<FileEntity> files;
    long total;
    int page;
    int pageSize;
}
