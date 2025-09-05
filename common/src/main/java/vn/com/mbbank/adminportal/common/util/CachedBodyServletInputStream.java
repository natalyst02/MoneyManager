package vn.com.mbbank.adminportal.common.util;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;

public class CachedBodyServletInputStream extends ServletInputStream {

  private final ByteArrayInputStream buffer;

  public CachedBodyServletInputStream(byte[] body) {
    this.buffer = new ByteArrayInputStream(body);
  }

  @Override
  public int read() {
    return buffer.read();
  }

  @Override
  public boolean isFinished() {
    return buffer.available() == 0;
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setReadListener(ReadListener readListener) {
    // Not needed
  }
}
