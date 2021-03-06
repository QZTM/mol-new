package com.mol.ddmanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.IdWorker;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@MapperScan(value = "com.mol.ddmanage.mapper")
@Configuration
@EnableFeignClients
@EnableEurekaClient
@EnableDiscoveryClient
public class DdManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DdManageApplication.class, args);

    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        factory.setMaxFileSize("102400KB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("1024000KB");
        return factory.createMultipartConfig();
    }
    @Bean("idWork")
    public IdWorker getIdWorker()
    {
        return new IdWorker();
    }
    public void rrrr()
    {
        String hh="";
    }
/*    @Bean("ContractHandler")
    public ContractHandler getContractHandler()
    {
        return new ContractHandler();
    }*/
}
