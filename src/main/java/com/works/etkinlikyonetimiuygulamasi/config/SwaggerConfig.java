package com.works.etkinlikyonetimiuygulamasi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Etkinlik Planlama Uygulaması API")
                        .version("1.0.0")
                        .description("Kullanıcıların etkinlik oluşturabildiği, katılım sağlayabildiği ve yönetebildiği sistemin RESTful API Dokümantasyonu.")
                        .contact(new Contact()
                                .name("Geliştirici")
                                .email("ornek@email.com")));
    }
}