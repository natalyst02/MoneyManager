package vn.com.mbbank.adminportal.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(servers = @Server(url = "${springdoc.server-url}"), info = @Info(title = "Admin Portal API", version = "1.0", description = "This lists all the Payment Admin Portal API Calls"))
public class OpenAPIConfiguration {
  private static final String OAUTH_SCHEME = "admin-portal-auth";

  @Value("${oauth2.internal-url}")
  String oauthURL;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes(OAUTH_SCHEME, new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("Oauth2 flow")
                .flows(new OAuthFlows()
                    .password(new OAuthFlow()
                        .tokenUrl(oauthURL)
                        .scopes(new Scopes())
                    )
                )
            )
        )
        .security(List.of(
            new SecurityRequirement().addList(OAUTH_SCHEME)))
        .info(new io.swagger.v3.oas.models.info.Info()
            .title("Admin Portal API")
            .description("This lists all the Payment Admin Portal API Calls. The Calls are OAuth2 secured, so please use your client ID and Secret to test them out.")
            .termsOfService("terms")
            .contact(new Contact().name("Developer: MBBANK"))
            .license(new License().name("GNU"))
            .version("1.0")
        );
  }
}
