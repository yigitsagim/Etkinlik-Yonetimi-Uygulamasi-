package com.works.etkinlikyonetimiuygulamasi.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppBeans {
    @Bean(name = "model")
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
