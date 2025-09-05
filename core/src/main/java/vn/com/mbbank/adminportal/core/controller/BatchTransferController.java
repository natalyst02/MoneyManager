package vn.com.mbbank.adminportal.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.service.ProxyService;
import vn.com.mbbank.adminportal.common.service.impl.ProxyServiceImpl;
import vn.com.mbbank.adminportal.common.service.internal.ErrorMappingConfigServiceInternal;
import vn.com.mbbank.adminportal.common.util.RestClient;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/batch-transfer")
public class BatchTransferController {
  private final ProxyService proxyService;

  public BatchTransferController(RestClient restClient,
                                 @Value("${batch-transfer.proxy-source-prefix}") String sourcePrefix,
                                 @Value("${batch-transfer.url}") String url,
                                 @Value("#{'${batch-transfer.oauth2-url}' ne ''  ? '${batch-transfer.oauth2-url}' : '${oauth2.url}'}") String oauthUrl,
                                 @Value("${batch-transfer.client-id}") String clientId,
                                 @Value("${batch-transfer.client-secret}") String clientSecret,
                                 ErrorMappingConfigServiceInternal errorMappingConfigServiceInternal) {
    this.proxyService = new ProxyServiceImpl(restClient, sourcePrefix, url, oauthUrl, clientId, clientSecret, errorMappingConfigServiceInternal);
  }

  @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
  public CompletableFuture<Response<Object>> send(HttpServletRequest request) {
    return proxyService.send(request);
  }
}
