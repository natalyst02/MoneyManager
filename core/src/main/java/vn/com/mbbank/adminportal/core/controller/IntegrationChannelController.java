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
@RequestMapping("/integration-channels/**")
public class IntegrationChannelController {
  private final ProxyService proxyService;

  public IntegrationChannelController(RestClient restClient,
                                      @Value("${routing-transfer.proxy-source-prefix}") String sourcePrefix,
                                      @Value("${routing-transfer.url}") String url,
                                      @Value("#{'${routing-transfer.oauth2-url}' ne ''  ? '${routing-transfer.oauth2-url}' : '${oauth2.url}'}") String oauthUrl,
                                      @Value("${routing-transfer.client-id}") String clientId,
                                      @Value("${routing-transfer.client-secret}") String clientSecret,
                                      ErrorMappingConfigServiceInternal errorMappingConfigServiceInternal) {
    this.proxyService = new ProxyServiceImpl(restClient, sourcePrefix, url, oauthUrl, clientId, clientSecret, errorMappingConfigServiceInternal);
  }

  @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
  public CompletableFuture<Response<Object>> forward(HttpServletRequest request) {
    return proxyService.send(request);
  }
}
