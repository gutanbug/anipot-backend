package org.anipotbackend.global.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "[Anipot] 백엔드 API",
                version = SwaggerConfiguration.API_VERSION,
                description = "Anipot 백엔드 REST API"
        ),
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(url = "/", description = "로컬 서버"),
                // todo : 개발 서버 추가
        }
)
public class SwaggerConfiguration {
    public static final String API_VERSION = "v1.0.0";

    @Bean
    GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch("/user/**")
                .build();
    }
}
