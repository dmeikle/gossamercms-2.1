package com.gossamercms.emails.config;

import com.gossamercms.mvc.autoconfig.*;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
@EnableModuleModels(basePackage = "com.gossamercms.emails.models")
@EnableModuleConverters(basePackage = "com.gossamercms.emails.converters")
@EnableModuleDbServices(basePackage = "com.gossamercms.emails.data")
@EnableModuleHandlers(basePackage = "com.gossamercms.emails.handlers")
@EnableModuleServices(basePackage = "com.gossamercms.emails.services")
@EnableModuleProviders(basePackage = "com.gossamercms.emails.providers")
@EnableModuleEventListeners(basePackage = "com.gossamercms.emails.listeners")
public class EmailsAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> EmailsAutoConfig LOADED");
        System.out.println("************************************************************");
    }

}
