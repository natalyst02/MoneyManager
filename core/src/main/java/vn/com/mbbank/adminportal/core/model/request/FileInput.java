package vn.com.mbbank.adminportal.core.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class FileInput {
    int page;
    int pageSize;
    String fileName;
    String fromDate;
    String toDate;
}
