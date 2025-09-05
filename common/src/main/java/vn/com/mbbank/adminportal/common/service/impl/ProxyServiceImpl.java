package vn.com.mbbank.adminportal.common.service.impl;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.runtime.Generics;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.exception.ServiceException;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.oauth.OAuthClient;
import vn.com.mbbank.adminportal.common.service.ProxyService;
import vn.com.mbbank.adminportal.common.service.internal.ErrorMappingConfigServiceInternal;
import vn.com.mbbank.adminportal.common.util.Authentications;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.common.util.CompletableFutures;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.common.util.ForwardBodyHandler;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.RestClient;
import vn.com.mbbank.adminportal.common.util.Tracing;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class ProxyServiceImpl implements ProxyService {
  private static final Set<String> DISALLOWED_HEADERS_SET;
  private static final JsonReader.ReadObject<Response<Object>> responseReader
      = Json.findReader(Generics.makeParameterizedType(Response.class, Object.class));

  static {
    TreeSet<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    treeSet.addAll(Set.of("authorization", "connection", "content-length", "date", "expect", "from", "host", "upgrade", "via", "warning"));
    DISALLOWED_HEADERS_SET = Collections.unmodifiableSet(treeSet);
  }

  private final HttpClient httpClient;
  private final String baseUrl;
  private final int ignorePrefixLength;
  private final OAuthClient oAuthClient;
  private final ErrorMappingConfigServiceInternal errorMappingConfigService;

  public ProxyServiceImpl(RestClient restClient,
                          String sourcePrefix,
                          String baseUrl,
                          String oauth2Url,
                          String clientId,
                          String clientSecret,
                          ErrorMappingConfigServiceInternal errorMappingConfigService) {
    this.httpClient = restClient.getHttpClient();
    this.baseUrl = baseUrl;
    ignorePrefixLength = sourcePrefix.length();
    this.errorMappingConfigService = errorMappingConfigService;
    this.oAuthClient = OAuthClient.builder()
        .setRestClient(restClient)
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setTokenApiUrl(URI.create(oauth2Url))
        .setGrantType(Constant.OAUTH_GRANT_TYPE)
        .setUseCache(true)
        .build();
  }

  @Override
  public CompletableFuture<Void> forward(HttpServletRequest request, HttpServletResponse response) {
    return doForward(request, response, newBodyPublisher(request));
  }

  @Override
  public CompletableFuture<Void> forward(HttpServletRequest request, byte[] requestBody, HttpServletResponse response) {
    return doForward(request, response, HttpRequest.BodyPublishers.ofByteArray(requestBody));
  }

  @Override
  public CompletableFuture<Response<Object>> send(HttpServletRequest request) {
    return doSend(request, newBodyPublisher(request));
  }

  private CompletableFuture<Response<Object>> doSend(HttpServletRequest request, HttpRequest.BodyPublisher bodyPublisher) {
    var username = Authentications.getUsername();
    return oAuthClient.getAccessToken()
        .thenCompose(accessToken -> {
          var forwardReq = HttpRequest.newBuilder(resolveTargetUri(request))
              .method(request.getMethod(), bodyPublisher);
          forwardHeaders(forwardReq, request);
          forwardReq.setHeader("Authorization", "Bearer " + accessToken.getToken());
          username.ifPresent(result -> forwardReq.setHeader(Constant.X_AUTH_USER, result));
          return Tracing.instrument(
              httpClient.sendAsync(forwardReq.build(), HttpResponse.BodyHandlers.ofByteArray())
                  .thenApply(response -> {
                    var responseBody = Json.decode(response.body(), responseReader);
                    if (response.statusCode() == 200) {
                      return responseBody;
                    }
                    var errorMapping = errorMappingConfigService.getByCode(responseBody.getSoaErrorCode());
                    if (errorMapping != null) {
                      responseBody.setSoaErrorDesc(errorMapping.getDescriptionVi());
                    }
                    throw CompletableFutures.toCompletionException(new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVICE_ERROR, CommonErrorCode.INTERNAL_SERVICE_ERROR.message(), new ServiceException(responseBody)));
                  })
          );
        });
  }

  HttpRequest.BodyPublisher newBodyPublisher(HttpServletRequest request) {
    long contentLength = request.getContentLengthLong();
    if (contentLength == 0) {
      return HttpRequest.BodyPublishers.noBody();
    }
    var bodyPublisher = HttpRequest.BodyPublishers.ofInputStream(() -> {
      try {
        return request.getInputStream();
      } catch (IOException e) {
        log.error("can't get input stream of request to forward", e);
        return null;
      }
    });
    if (contentLength > 0) {
      bodyPublisher = HttpRequest.BodyPublishers.fromPublisher(bodyPublisher, contentLength);
    }
    return bodyPublisher;
  }

  private CompletableFuture<Void> doForward(HttpServletRequest request, HttpServletResponse response, HttpRequest.BodyPublisher bodyPublisher) {
    var username = Authentications.getUsername();
    return oAuthClient.getAccessToken()
        .thenCompose(accessToken -> {
          var forwardReq = HttpRequest.newBuilder(resolveTargetUri(request))
              .method(request.getMethod(), bodyPublisher);
          forwardHeaders(forwardReq, request);
          forwardReq.setHeader("Authorization", "Bearer " + accessToken.getToken());
          username.ifPresent(result -> forwardReq.setHeader(Constant.X_AUTH_USER, result));
          try {
            return Tracing.instrument(
                httpClient.sendAsync(forwardReq.build(), new ForwardBodyHandler(response))
                    .thenAccept(resp -> {
                    })
            );
          } catch (IOException e) {
            throw new NSTCompletionException("Can't get output stream of http response", e);
          }
        });
  }

  private void forwardHeaders(HttpRequest.Builder builder, HttpServletRequest request) {
    var headers = request.getHeaderNames();
    String header;
    while (headers.hasMoreElements()) {
      header = headers.nextElement();
      if (DISALLOWED_HEADERS_SET.contains(header)) {
        continue;
      }
      var values = request.getHeaders(header);
      while (values.hasMoreElements()) {
        builder.header(header, values.nextElement());
      }
    }
  }

  private URI resolveTargetUri(HttpServletRequest request) {
    var requestURI = request.getRequestURI();
    var builder = new StringBuilder().append(baseUrl).append(requestURI, ignorePrefixLength, requestURI.length());
    if (request.getQueryString() != null) {
      builder.append('?').append(request.getQueryString());
    }
    return URI.create(builder.toString());
  }
}