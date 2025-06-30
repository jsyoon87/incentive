package com.mintit.incentive.configure.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

@Slf4j
public class ServerWebCustomWrapper extends ServerWebExchangeDecorator {

    public ServerWebExchange delegate;

    public ServerWebCustomWrapper(ServerWebExchange delegate) {
        super(delegate);
        this.delegate = delegate;
        if (Objects.equals(delegate.getRequest().getMethod(), HttpMethod.GET)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                log.info("Request Query Params = {} {}", delegate.getRequest()
                                                                 .getPath(), mapper.writerWithDefaultPrettyPrinter()
                                                                                   .writeValueAsString(delegate.getRequest()
                                                                                                               .getQueryParams()));
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException Error - ", e);
            }
        }
    }


    @Override
    public ServerHttpRequest getRequest() {
        return new RequestLoggingDecorator(delegate.getRequest());
    }

}
