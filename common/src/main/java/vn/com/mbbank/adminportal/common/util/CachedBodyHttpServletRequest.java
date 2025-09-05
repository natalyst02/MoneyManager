package vn.com.mbbank.adminportal.common.util;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.io.InputStream;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

  private final byte[] cachedBody;

  public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
    super(request);
    InputStream requestInputStream = request.getInputStream();
    this.cachedBody = requestInputStream.readAllBytes();
  }

  @Override
  public ServletInputStream getInputStream() {
    return new CachedBodyServletInputStream(this.cachedBody);
  }

  public byte[] getCachedBody() {
    return this.cachedBody;
  }
}
