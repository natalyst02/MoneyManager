package vn.com.mbbank.adminportal.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.mbbank.adminportal.common.service.ProxyService;
import vn.com.mbbank.adminportal.common.service.impl.ProxyServiceImpl;
import vn.com.mbbank.adminportal.common.service.internal.ErrorMappingConfigServiceInternal;
import vn.com.mbbank.adminportal.common.util.RestClient;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/inhouse-transfer")
public class InhouseTransferController {
  private final ProxyService proxyService;
  public InhouseTransferController(RestClient restClient,
                                   @Value("${inhouse-transfer.proxy-source-prefix}") String sourcePrefix,
                                   @Value("${inhouse-transfer.url}") String url,
                                   @Value("#{'${inhouse-transfer.oauth2-url}' ne ''  ? '${inhouse-transfer.oauth2-url}' : '${oauth2.url}'}") String oauthUrl,
                                   @Value("${inhouse-transfer.client-id}") String clientId,
                                   @Value("${inhouse-transfer.client-secret}") String clientSecret,
                                   ErrorMappingConfigServiceInternal errorMappingConfigServiceInternal) {
    this.proxyService = new ProxyServiceImpl(restClient, sourcePrefix, url, oauthUrl, clientId, clientSecret, errorMappingConfigServiceInternal);
  }

  @GetMapping(value = {"/configs/**", "/config-audits/**"})
  public CompletableFuture<Void> forwardGetRequest(HttpServletRequest request, HttpServletResponse response) {
    return proxyService.forward(request, response);
  }

  @PutMapping(value = {"/configs/**"})
  public CompletableFuture<Void> forwardPutRequest(HttpServletRequest request, HttpServletResponse response) {
    return proxyService.forward(request, response);
  }
}
