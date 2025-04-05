package com.example.console.config;

import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import com.example.console.interceptor.ConsoleInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.example.module.service.UserService;

import java.util.List;


@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final ApplicationArguments appArguments;

    public WebMvcConfiguration(ApplicationArguments appArguments) {
        this.appArguments = appArguments;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(newUserAuthResolver());
    }

    @Bean
    public UserAuthorityResolver newUserAuthResolver() {
        return new UserAuthorityResolver(appArguments);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ConsoleInterceptor())
                .addPathPatterns("/console/**")
                .excludePathPatterns(
                        "/app/user/login",
                        "/app/user/register",
                        "/app/user/logout"
                );
    }
}
