package vn.com.mbbank.adminportal.core.thirdparty.presidio.impl;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Generics;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.RestApiHelper;
import vn.com.mbbank.adminportal.common.util.RestClient;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.PresidioClient;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.model.TextAnalysisDTO;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.model.TextAnalysisInput;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class PresidioClientImpl implements PresidioClient {

    private static final JsonWriter.WriteObject<TextAnalysisInput> analyzeTextReqWriter = Json.findWriter(TextAnalysisInput.class);
    private static final JsonReader.ReadObject<List<TextAnalysisDTO>> analyzeTextRespReader = Json.findReader(Generics.makeParameterizedType(List.class, TextAnalysisDTO.class));
    private final RestClient restClient;
    private final URI uri;

    public PresidioClientImpl(RestClient restClient, String uri) {
        this.restClient = restClient;
        this.uri = URI.create(uri);
    }


    @Override
    public CompletableFuture<List<TextAnalysisDTO>> analyzeText(TextAnalysisInput input) {
        return restClient.post(URI.create(uri + "/analyze"),
                        Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE),
                        input,
                        analyzeTextReqWriter)
                .handle((httpResponse, throwable) -> {
                    if (throwable != null) {
                        RestApiHelper.handleException(throwable, ErrorCode.ANALAYZE_TEXT_ERROR, ErrorCode.ANALAYZE_TEXT_ERROR::message,
                                ErrorCode.ANALAYZE_TEXT_ERROR, ErrorCode.ANALAYZE_TEXT_ERROR::message);
                    }
                    var statusCode = httpResponse.statusCode();
                    if (statusCode == 200) {
                        var response = Json.decode(httpResponse.body(), analyzeTextRespReader, e ->
                                new NSTCompletionException(new PaymentPlatformException(ErrorCode.ANALAYZE_TEXT_ERROR,
                                        "Can not get analyzeTextResp due: " + new String(httpResponse.body(), StandardCharsets.UTF_8))));
//                        if (!response.isEmpty()) {
                            return response;
//                        }
//                        throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.ANALAYZE_TEXT_ERROR, ErrorCode.ANALAYZE_TEXT_ERROR.message()));
                    }
                    throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.ANALAYZE_TEXT_ERROR, "Can not get user info due: " + new String(httpResponse.body(), StandardCharsets.UTF_8)));
                });
    }
}
