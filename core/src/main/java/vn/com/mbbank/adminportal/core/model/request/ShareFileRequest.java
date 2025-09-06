package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
public class ShareFileRequest {
    @NotNull
    @Length(max = 20)
    private String username;
    @NotNull
    @Length(max = 20)
    private HashSet<String> fileIds;
}
