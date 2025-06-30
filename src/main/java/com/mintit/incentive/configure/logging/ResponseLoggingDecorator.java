package com.mintit.incentive.configure.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ResponseLoggingDecorator extends ServerHttpResponseDecorator {

    public ResponseLoggingDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> buffer) {
        return super.writeWith(Flux.from(buffer).doOnNext(dataBuffer -> {
            try (ByteArrayOutputStream bas = new ByteArrayOutputStream()
            ) {
                Channels.newChannel(bas).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                log.info("Response payload = {}", new String(bas.toByteArray(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
