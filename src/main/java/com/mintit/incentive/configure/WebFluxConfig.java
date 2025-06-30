package com.mintit.incentive.configure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
@ConditionalOnClass(EnableWebFlux.class)
@ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.REACTIVE)
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configure) {
        ReactivePageableHandlerMethodArgumentResolver reactivePageableHandlerMethodArgumentResolver = new ReactivePageableHandlerMethodArgumentResolver();
        reactivePageableHandlerMethodArgumentResolver.setFallbackPageable(PageRequest.of(0, 15));
        configure.addCustomResolver(reactivePageableHandlerMethodArgumentResolver);

    }
    
}
