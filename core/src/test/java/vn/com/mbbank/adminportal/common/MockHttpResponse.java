package vn.com.mbbank.adminportal.common;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class MockHttpResponse implements HttpResponse<byte[]> {
  private int status;
  private HttpRequest request;
  private HttpResponse<byte[]> previousResponse;
  private HttpHeaders headers;
  private byte[] body;
  private SSLSession sslSession;
  private URI uri;
  private HttpClient.Version version;

  @Override
  public int statusCode() {
    return status;
  }

  @Override
  public HttpRequest request() {
    return request;
  }

  @Override
  public Optional<HttpResponse<byte[]>> previousResponse() {
    return Optional.ofNullable(previousResponse);
  }

  @Override
  public HttpHeaders headers() {
    return headers;
  }

  @Override
  public byte[] body() {
    return body;
  }

  @Override
  public Optional<SSLSession> sslSession() {
    return Optional.ofNullable(sslSession);
  }

  @Override
  public URI uri() {
    return uri;
  }

  @Override
  public HttpClient.Version version() {
    return version;
  }
}
