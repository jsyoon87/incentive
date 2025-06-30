package com.mintit.incentive.configure.logging;

import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
import reactor.util.context.Context;

/***
 * Spring-cloud-start-sleuth 대체
 */
//@Component
public class CustomHttpHandlerDecorator implements HttpHandlerDecoratorFactory {

    private static final String MDC_KEY_TRACE_ID = "traceId";

    @Override
    public HttpHandler apply(HttpHandler httpHandler) {
        return (request, response) -> httpHandler.handle(request, response)
                                                 .contextWrite(context -> {
                                                     final String traceId = getTraceId();
                                                     MDC.put(MDC_KEY_TRACE_ID, traceId);
                                                     return Context.of(MDC_KEY_TRACE_ID, traceId);
                                                 });
    }

    private String getTraceId() {
        return UUID.randomUUID().toString();
    }
}
