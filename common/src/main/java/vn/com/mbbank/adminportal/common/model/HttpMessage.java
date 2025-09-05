package vn.com.mbbank.adminportal.common.model;

import com.dslplatform.json.CompiledJson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@CompiledJson
public class HttpMessage<T> {
  private String sourceAppIp;
  private String destAppIp;
  private int destAppPort;
  private String httpPath;
  private String httpMethod;
  private int responseCode;
  private Map<String, String> header = new HashMap<>();
  private T body;

  public HttpMessage(HttpServletRequest httpRequest) {
    this(httpRequest, null);
  }

  public HttpMessage(HttpServletRequest httpRequest, T body) {
    this.sourceAppIp = httpRequest.getRemoteHost();
    this.destAppIp = httpRequest.getLocalAddr();
    this.destAppPort = httpRequest.getLocalPort();
    this.httpMethod = httpRequest.getMethod();
    this.httpPath = httpRequest.getRequestURI();
    var queryString = httpRequest.getQueryString();
    if (queryString != null) {
      this.httpPath += "?" + queryString;
    }
    var headerNames = httpRequest.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      var headerName = headerNames.nextElement();
      if (headerName.equalsIgnoreCase("Authorization")) {
        this.header.put(headerName, "<<Not recorded to log>>");
      } else {
        this.header.put(headerName, httpRequest.getHeader(headerName));
      }
    }

    this.body = body;
  }

  public HttpMessage(HttpServletRequest httpRequest, HttpServletResponse httpResponse, T body) {
    this.sourceAppIp = httpRequest.getRemoteHost();
    this.destAppIp = httpRequest.getLocalAddr();
    this.destAppPort = httpRequest.getLocalPort();
    this.httpMethod = httpRequest.getMethod();
    this.httpPath = httpRequest.getRequestURI();
    this.responseCode = httpResponse.getStatus();
    httpResponse.getHeaderNames().forEach(headerName -> this.header.put(headerName, httpResponse.getHeader(headerName)));
    this.body = body;
  }
}
