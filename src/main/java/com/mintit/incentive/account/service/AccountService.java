package com.mintit.incentive.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mintit.incentive.account.model.AccountModel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
@Slf4j
public class AccountService {

    public Mono<Map<String, Object>> transferBank(AccountModel accountModel) {
        /*
         * TODO 실제 테스트는 확인 못함 - 로컬 환경에서 아이피가 차단되어 확인불가 기타 Response 값은 확인완료
         */
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> requestMap = new HashMap<>();
            List<Map<String, Object>> requestData = Collections.singletonList(new HashMap<String, Object>() {{
                put("TRSC_SEQ_NO", "000001");
                put("BANK_CD", accountModel.getBankCode());
                put("SEARCH_ACCT_NO", accountModel.getAccountNo());
                put("ACNM_NO", "");
                put("ICHE_AMT", 0);
            }});
            requestMap.put("SECR_KEY", "PCZvTsV2LuYKwaiSmXeI");
            requestMap.put("KEY", "ACCTNM_RCMS_WAPI");
            requestMap.put("REQ_DATA", requestData);

            SslContext sslContext = SslContextBuilder.forClient()
                                                     .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                                     .build();
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

            return WebClient.builder()
                            .clientConnector(new ReactorClientHttpConnector(httpClient))
                            .build()
                            .post()
                            .uri("https://dev2.coocon.co.kr:8443/sol/gateway/acctnm_rcms_wapi.jsp")
                            .body(BodyInserters.fromFormData("JSONData", mapper.writeValueAsString(requestMap)))
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(s -> {
                                try {
                                    return Mono.just(mapper.readValue(s.replaceAll("\r\n", "")
                                                                       .replaceAll("\r", "")
                                                                       .replaceAll("\n", ""), new TypeReference<Map<String, Object>>() {}));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            });
        } catch (JsonProcessingException | SSLException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("ERROR", true);
            return Mono.just(result);
        }
    }
}
