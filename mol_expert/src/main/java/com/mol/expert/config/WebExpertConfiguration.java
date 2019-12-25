package com.mol.expert.config;

import com.mol.expert.interceptor.SessionExpertInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebExpertConfiguration implements WebMvcConfigurer {

    private static final Logger logger= LoggerFactory.getLogger(WebExpertConfiguration.class);
    @Bean
    SessionExpertInterceptor getSessionExpertInterceptor(){
        return new SessionExpertInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("expertSession拦截器启动");
        registry.addInterceptor(getSessionExpertInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("static/**","static/**/**","/expert/findAll","/microApp/**","**/img/**","/index/findAll","**/js/**","**/css/**","/static/upload/**","/pdf/**","/templates/**","/css/**","/js/**","/img/**","/layui/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/upload/**");
        registry.addResourceHandler("/index/**").addResourceLocations("classpath:/templates");
    }
}
