package vn.com.mbbank.adminportal.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.service.ProxyService;
import vn.com.mbbank.adminportal.common.service.impl.ProxyServiceImpl;
import vn.com.mbbank.adminportal.common.service.internal.ErrorMappingConfigServiceInternal;
import vn.com.mbbank.adminportal.common.util.RestClient;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/napas/ibft-reconcile")
public class IbftReconcileController {
  private final ProxyService proxyService;

  public IbftReconcileController(RestClient restClient,
                                 @Value("${ibft-reconcile.proxy-source-prefix}") String sourcePrefix,
                                 @Value("${ibft-reconcile.url}") String url,
                                 @Value("#{'${ibft-reconcile.oauth2-url}' ne ''  ? '${ibft-reconcile.oauth2-url}' : '${oauth2.url}'}") String oauthUrl,
                                 @Value("${ibft-reconcile.client-id}") String clientId,
                                 @Value("${ibft-reconcile.client-secret}") String clientSecret,
                                 ErrorMappingConfigServiceInternal errorMappingConfigServiceInternal) {
    this.proxyService = new ProxyServiceImpl(restClient, sourcePrefix, url, oauthUrl, clientId, clientSecret, errorMappingConfigServiceInternal);
  }

  @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
  public CompletableFuture<Response<Object>> send(HttpServletRequest request) {
    return proxyService.send(request);
  }

  @PostMapping(value = {"/out/dispute/export"})
  public CompletableFuture<Void> forward(HttpServletRequest request, HttpServletResponse response) {
    return proxyService.forward(request, response);
  }

  @GetMapping(value = {"/out/file/**", "/out/file/download/**"})
  public CompletableFuture<Void> forward2(HttpServletRequest request, HttpServletResponse response) {
    return proxyService.forward(request, response);
  }
}