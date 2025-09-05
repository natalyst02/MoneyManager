package vn.com.mbbank.adminportal.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/citad-transfer")
public class CitadTransferController {
  private final ProxyService proxyService;

  public CitadTransferController(RestClient restClient,
                                 @Value("${citad-transfer.proxy-source-prefix}") String sourcePrefix,
                                 @Value("${citad-transfer.url}") String url,
                                 @Value("#{'${citad-transfer.oauth2-url}' ne ''  ? '${citad-transfer.oauth2-url}' : '${oauth2.url}'}") String oauthUrl,
                                 @Value("${citad-transfer.client-id}") String clientId,
                                 @Value("${citad-transfer.client-secret}") String clientSecret,
                                 ErrorMappingConfigServiceInternal errorMappingConfigServiceInternal) {
    this.proxyService = new ProxyServiceImpl(restClient, sourcePrefix, url, oauthUrl, clientId, clientSecret, errorMappingConfigServiceInternal);
  }

  @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
  public CompletableFuture<Response<Object>> send(HttpServletRequest request) {
    return proxyService.send(request);
  }

  @GetMapping(value = {"/transactions/export", "/configs/export/**", "/blacklist-accounts/export",
      "/whitelist-accounts/export", "/state-treasuries/export", "/whitelist-categories/export"})
  public CompletableFuture<Void> forward(HttpServletRequest request, HttpServletResponse response) {
    return proxyService.forward(request, response);
  }
}
