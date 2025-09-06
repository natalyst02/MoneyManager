package vn.com.mbbank.adminportal.core.thirdparty.presidio.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextAnalysisInput {
    private String text;
    private String language;
}
