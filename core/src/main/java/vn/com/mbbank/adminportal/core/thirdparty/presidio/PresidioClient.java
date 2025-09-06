package vn.com.mbbank.adminportal.core.thirdparty.presidio;

import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.response.GetUserInfoResponse;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.model.TextAnalysisDTO;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.model.TextAnalysisInput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PresidioClient {
    CompletableFuture<List<TextAnalysisDTO>> analyzeText(TextAnalysisInput input);
}
