package com.mintit.incentive.terms.controller;

import com.mintit.incentive.common.model.ResponseModel;
import com.mintit.incentive.common.type.ResponseType;
import com.mintit.incentive.common.util.ConvertUtil;
import com.mintit.incentive.common.util.JWTUtil;
import com.mintit.incentive.terms.model.TermsChoice;
import com.mintit.incentive.terms.model.TermsCreate;
import com.mintit.incentive.terms.model.TermsModel;
import com.mintit.incentive.terms.service.TermsService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermsController {

    final JWTUtil jwtUtil;

    final TermsService termsService;

    final ConvertUtil convertUtil;

    @GetMapping()
    public Mono<ResponseModel<Map<String, Object>>> list(@RequestParam(value="userGroup", required=false) String userGroup, Pageable pageable) {
        LinkedHashMap<String, Object> queryParam = new LinkedHashMap<>();
        if (StringUtils.hasText(userGroup)) {
            queryParam.put("userGroup", userGroup);
        }
        ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();

        return this.termsService.getPageList(queryParam, pageable)
                                .flatMap(result -> Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                                                          .setResult(result)));
        
    }

    @Validated(TermsCreate.class)
    @PostMapping()
    public Mono<ResponseModel<Map<String, Object>>> create(@RequestBody @Valid final TermsModel termsModel, @RequestHeader("Authorization") String token) {
        String regId = jwtUtil.setJwtParser("jwt").getLoginUuidByJwt(token.replace("Bearer", ""));
        termsModel.setRegId(regId);
        return this.termsService.create(termsModel).flatMap(terms -> {
            ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
            terms.setEndDt(LocalDate.parse(terms.getEndDt(), DateTimeFormatter.ofPattern("yyyyMMdd"))
                                    .atStartOfDay()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                          .setResult(this.convertUtil.toMap(terms)));
        });
    }

    @Validated(TermsChoice.class)
    @PostMapping("choice")
    public Mono<ResponseModel<Map<String, String>>> choice(@RequestBody @Valid final TermsModel termsModel, @RequestHeader("Authorization") String token) {
        String userUuid = jwtUtil.setJwtParser("jwt")
                                 .getLoginUuidByJwt(token.replace("Bearer", ""));
        termsModel.setUserUuid(userUuid);
        return this.termsService.choice(termsModel).flatMap(result -> {
            ResponseModel<Map<String, String>> responseModel = new ResponseModel<>();
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("message", result);
            return Mono.just(responseModel.setStatus(result.equals("정상적으로 변경 하였습니다")
                                                     ? ResponseType.SUCCESS.code()
                                                     : ResponseType.FAIL.code())
                                          .setResult(resultMap));
        });

    }
}
