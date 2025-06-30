package com.mintit.incentive.configure;

import com.mintit.incentive.configure.logging.ServerWebCustomWrapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFlux 에서 사용되는 Filter 적용
 */
@Configuration
public class ReactiveWebFilter implements WebFilter {

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        //        ServerWebExchangeDecorator decorator = new ServerWebExchangeDecorator(serverWebExchange) {
        //            @Override
        //            public ServerHttpRequest getRequest() {
        //                return new RequestLoggingDecorator(serverWebExchange.getRequest());
        //            }
        //            //Response Log 는 주석처리함 - 불필요 로그라 생각하여
        //            //            public ServerHttpResponse getResponse() {
        //            //                return new ResponseLoggingDecorator(serverWebExchange.getResponse());
        //            //            }
        //        };
        return webFilterChain.filter(new ServerWebCustomWrapper(serverWebExchange));
    }
}
