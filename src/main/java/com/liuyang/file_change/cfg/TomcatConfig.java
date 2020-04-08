package com.liuyang.file_change.cfg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.io.File;

@Configuration
public class TomcatConfig {

    @Value("${tomcatCache}")
    private String TOMCAT_TEMP;

    @Value("5MB")
    private String maxFileSize;

    @Value("20MB")
    private String maxRequestSize;

    @Bean
    public MultipartConfigElement multipartConfigElement(){
        File file = new File(TOMCAT_TEMP);
        if(file.exists()){
            file.mkdirs();
        }
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse(maxFileSize));
        factory.setMaxRequestSize(DataSize.parse(maxRequestSize));
        factory.setLocation(TOMCAT_TEMP);
        return factory.createMultipartConfig();
    }
}
