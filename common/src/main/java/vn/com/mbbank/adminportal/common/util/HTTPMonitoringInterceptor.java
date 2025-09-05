package vn.com.mbbank.adminportal.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import vn.com.mbbank.adminportal.common.model.HttpMessage;
import vn.com.mbbank.adminportal.common.model.PapUser;
import vn.com.mbbank.adminportal.common.model.response.Response;

import java.lang.reflect.Type;

@ControllerAdvice
@Log4j2
@RequiredArgsConstructor
public class HTTPMonitoringInterceptor extends RequestBodyAdviceAdapter implements AsyncHandlerInterceptor, ResponseBodyAdvice<Response<?>> {

  private final ObjectMapper objectMapper;
  @Override
  public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;//TODO check file request
  }

  @Override
  public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
      var httpServletRequest = attributes.getRequest();
      if (LoggingHelper.shouldSkipLog(httpServletRequest)) {
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
      }
      httpServletRequest.setAttribute(LoggingHelper.START_TIME_KEY, System.nanoTime());
      ThreadContext.put(LoggingHelper.LOG_TYPE, "httprequest");
      ThreadContext.remove("duration");

      var authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.getPrincipal() instanceof PapUser papUser) {
        ThreadContext.put(LoggingHelper.AUTHENTICATION_USER, papUser.getUsername());
        ThreadContext.put(LoggingHelper.SOURCE_APP_ID_KEY, papUser.getSourceAppId());
        httpServletRequest.setAttribute(LoggingHelper.USER_NAME_KEY, papUser.getUsername());
        httpServletRequest.setAttribute(LoggingHelper.SOURCE_APP_ID_KEY, papUser.getSourceAppId());

        log.info(() -> SerializationUtils.serializeAsString(objectMapper, new HttpMessage<>(httpServletRequest, body)));
        ThreadContext.remove(LoggingHelper.LOG_TYPE);

        ThreadContext.remove(LoggingHelper.AUTHENTICATION_USER);
        ThreadContext.remove(LoggingHelper.SOURCE_APP_ID_KEY);
      } else {
        log.info(() -> SerializationUtils.serializeAsString(objectMapper, new HttpMessage<>(httpServletRequest, body)));
        ThreadContext.remove(LoggingHelper.LOG_TYPE);
      }
    }

    return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
  }

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return returnType.getParameterType().isAssignableFrom(Response.class);
  }

  @Override
  public Response<?> beforeBodyWrite(Response<?> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
    if (LoggingHelper.shouldLog(request) && request instanceof ServletServerHttpRequest servletServerHttpRequest && response instanceof ServletServerHttpResponse servletServerHttpResponse) {
      var httpServletRequest = servletServerHttpRequest.getServletRequest();
      var httpServletResponse = servletServerHttpResponse.getServletResponse();
      LoggingHelper.logResponse(httpServletRequest, httpServletResponse, body,
          () -> log.info(() -> SerializationUtils.serializeAsString(objectMapper, new HttpMessage<>(httpServletRequest, httpServletResponse, body))));
    }
    return body;

  }

}


