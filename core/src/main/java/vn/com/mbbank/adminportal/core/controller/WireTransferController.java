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
@RequestMapping("/wire-transfer/**")
public class WireTransferController {
  private final ProxyService proxyService;

  public WireTransferController(RestClient restClient,
                                @Value("${wire-transfer.proxy-source-prefix}") String sourcePrefix,
                                @Value("${wire-transfer.url}") String url,
                                @Value("#{'${wire-transfer.oauth2-url}' ne ''  ? '${wire-transfer.oauth2-url}' : '${oauth2.url}'}") String oauthUrl,
                                @Value("${wire-transfer.client-id}") String clientId,
                                @Value("${wire-transfer.client-secret}") String clientSecret,
                                ErrorMappingConfigServiceInternal errorMappingConfigServiceInternal) {
    this.proxyService = new ProxyServiceImpl(restClient, sourcePrefix, url, oauthUrl, clientId, clientSecret, errorMappingConfigServiceInternal);
  }

  @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
  public CompletableFuture<Response<Object>> forward(HttpServletRequest request) {
    return proxyService.send(request);
  }
}
