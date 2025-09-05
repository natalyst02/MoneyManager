package vn.com.mbbank.adminportal.common.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.com.mbbank.adminportal.common.model.response.Response;

import java.util.concurrent.CompletableFuture;

public interface ProxyService {
  CompletableFuture<Void> forward(HttpServletRequest request, HttpServletResponse response);

  CompletableFuture<Void> forward(HttpServletRequest request, byte[] requestBody, HttpServletResponse response);

  CompletableFuture<Response<Object>> send(HttpServletRequest request);
}