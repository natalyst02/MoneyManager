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
@RequestMapping("/ach/tktg-config/**")
public class AchTktgConfigController {

  private final ProxyService proxyService;

  public AchTktgConfigController(RestClient restClient,
                                 @Value("${ach-transfer.proxy-source-prefix}") String sourcePrefix,
                                 @Value("${ach-transfer.url}") String url,
                                 @Value("#{'${ach-transfer.oauth2-url}' ne ''  ? '${ach-transfer.oauth2-url}' : '${oauth2.url}'}") String oauthUrl,
                                 @Value("${ach-transfer.client-id}") String clientId,
                                 @Value("${ach-transfer.client-secret}") String clientSecret,
                                 ErrorMappingConfigServiceInternal errorMappingConfigServiceInternal) {
    this.proxyService = new ProxyServiceImpl(restClient, sourcePrefix, url, oauthUrl, clientId, clientSecret, errorMappingConfigServiceInternal);
  }

  @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
  public CompletableFuture<Response<Object>> forward(HttpServletRequest request) {
    return proxyService.send(request);
  }
}
