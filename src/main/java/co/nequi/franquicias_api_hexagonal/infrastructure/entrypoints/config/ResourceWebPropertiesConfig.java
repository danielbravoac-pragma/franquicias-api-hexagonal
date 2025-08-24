package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceWebPropertiesConfig {

    @Bean
    public WebProperties.Resources webProperties() {
        return new WebProperties.Resources();
    }
}
