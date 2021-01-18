package com.dfq.coeffi.cbs;

import com.dfq.coeffi.cbs.document.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class CbsApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(CbsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CbsApplication.class);
    }
}