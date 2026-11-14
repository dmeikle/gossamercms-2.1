package com.gossamercms.geolocation.config;

import com.gossamercms.mvc.autoconfig.EnableModuleProviders;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;


@EnableModuleProviders(basePackage = "com.gossamercms.geolocation.providers")
@AutoConfiguration
public class GeolocationAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> GeolocationAutoConfig LOADED");
        System.out.println("************************************************************");
    }
}
