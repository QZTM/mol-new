package com.mol.expert;

import com.mol.cache.CacheHandle;
import com.mol.notification.SendNotification;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableCaching
@EnableScheduling
@EnableAsync
@Configuration
@EnableDiscoveryClient
@EnableFeignClients
public class ExpertApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpertApplication.class, args);
    }

    @Bean
    public CacheHandle getCacheHandler(){
        return CacheHandle.getCacheHandle();
    }

    @Bean
    public SendNotification getSendNotification(){
        return SendNotification.getSendNotification();
    }
}
