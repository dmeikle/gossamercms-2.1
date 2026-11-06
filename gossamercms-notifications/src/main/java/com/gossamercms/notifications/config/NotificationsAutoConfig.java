package com.gossamercms.notifications.config;

import com.gossamercms.mvc.autoconfig.*;
import com.gossamercms.notifications.senders.CeilingbarMessageSender;
import com.gossamercms.notifications.senders.EmailMessageSender;
import com.gossamercms.notifications.senders.SmsMessageSender;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@AutoConfiguration
@Import({
        MessageTemplateConfiguration.class,
        EmailMessageSender.class,
        SmsMessageSender.class,
        CeilingbarMessageSender.class
})
//@EnableModuleControllers(basePackage = "com.gossamercms.notifications.controllers")
@EnableModuleModels(basePackage = "com.gossamercms.notifications.models")
@EnableModuleDbServices(basePackage = "com.gossamercms.notifications.data")
@EnableModuleHandlers(basePackage = "com.gossamercms.notifications.handlers")
@EnableModuleServices(basePackage = "com.gossamercms.notifications.services")
@EnableModuleProviders(basePackage = "com.gossamercms.notifications.providers")
@EnableModuleConverters(basePackage = "com.gossamercms.notifications.converters")
@EnableModuleEventListeners(basePackage = "com.gossamercms.notifications.listeners")
@ComponentScan(basePackages = "com.gossamercms.notifications.listeners") // Ensure all components are scanned
public class NotificationsAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> NotificationsAutoConfig LOADED");
        System.out.println("************************************************************");
    }

    @Configuration
    @EnableAsync
    public class MessagePersistenceConfiguration {
        @Bean
        public ThreadPoolTaskExecutor messagePersistenceExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(2);
            executor.setMaxPoolSize(4);
            executor.setQueueCapacity(500);
            executor.setThreadNamePrefix("message-persistence-");
            return executor;
        }
    }
}