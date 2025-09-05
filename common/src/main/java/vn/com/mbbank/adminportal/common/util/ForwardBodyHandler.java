package vn.com.mbbank.adminportal.common.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpResponse;

public class ForwardBodyHandler implements HttpResponse.BodyHandler<Void> {
  private final HttpServletResponse response;
  private final OutputStream outputStream;

  public ForwardBodyHandler(HttpServletResponse response) throws IOException {
    this.response = response;
    outputStream = response.getOutputStream();
  }

  @Override
  public HttpResponse.BodySubscriber<Void> apply(HttpResponse.ResponseInfo responseInfo) {
    response.setStatus(responseInfo.statusCode());
    responseInfo
        .headers()
        .map()
        .forEach((k, headerValues) -> {
          if (!"transfer-encoding".equalsIgnoreCase(k)) {
            headerValues.forEach(v -> response.addHeader(k, v));
          }
        });
    return new ForwardBodySubscriber(outputStream);
  }
}