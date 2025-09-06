package vn.com.mbbank.adminportal.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.mbbank.adminportal.common.mapper.Entity2ErrorMessageResponseMapper;
import vn.com.mbbank.adminportal.common.util.RequestLoggingFilter;
import vn.com.mbbank.adminportal.common.util.RestClient;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.PresidioClient;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.impl.PresidioClientImpl;
import vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.RoutingTransferClient;
import vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.impl.RoutingTransferClientImpl;

import java.net.http.HttpClient;

@Configuration
public class ApplicationConfiguration {
  @Autowired
  private ObjectMapper objectMapper;

  @Bean
  public RestClient restClient() {
    return new RestClient(HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build());
  }

  @Bean
  public Entity2ErrorMessageResponseMapper entity2ErrorMessageResponseMapper() {
    return Entity2ErrorMessageResponseMapper.INSTANCE;
  }

  @Bean
  public RequestLoggingFilter requestLoggingFilter() {
    return new RequestLoggingFilter(false, objectMapper)
        .setIncludePayload(true)
        .setIncludeResponse(true);
  }

  @Bean
  public RoutingTransferClient routingTransferClient(RestClient restClient,
                                                     @Value("#{'${routing-transfer.oauth2-url}' ne ''  ? '${routing-transfer.oauth2-url}' : '${oauth2.url}'}") String oauthUrl,
                                                     @Value("${routing-transfer.client-id}") String clientId,
                                                     @Value("${routing-transfer.client-secret}") String clientSecret,
                                                     @Value("${routing-transfer.url}") String url) {
    return new RoutingTransferClientImpl(restClient, oauthUrl, clientId, clientSecret, url);
  }


  @Bean
  public PresidioClient presidioClient(RestClient restClient,
                                       @Value("${presidioClient.url}") String url) {
    return new PresidioClientImpl(restClient, url);
  }
}