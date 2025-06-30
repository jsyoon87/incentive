package com.mintit.incentive.incentive.controller;

import com.mintit.incentive.common.model.ResponseModel;
import com.mintit.incentive.common.type.ResponseType;
import com.mintit.incentive.common.util.ConvertUtil;
import com.mintit.incentive.common.util.JWTUtil;
import com.mintit.incentive.incentive.model.IncentiveCreate;
import com.mintit.incentive.incentive.model.IncentiveModel;
import com.mintit.incentive.incentive.model.IncentiveWithdrawal;
import com.mintit.incentive.incentive.service.IncentiveService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("incentive")
@RequiredArgsConstructor
public class IncentiveController {


    private final IncentiveService incentiveService;

    private final ConvertUtil convertUtil;

    private final JWTUtil jwtUtil;

    @GetMapping()
    public Mono<ResponseModel<Map<String, Object>>> list(@RequestParam(value="status", required=false) String status, @RequestParam(value="startDt", required=false) String startDt, @RequestParam(value="endDt", required=false) String endDt, Pageable pageable, @RequestHeader("Authorization") String token) {
        LinkedHashMap<String, Object> queryParam = new LinkedHashMap<>();
        String userUuid = jwtUtil.setJwtParser("jwt")
                                 .getLoginUuidByJwt(token.replace("Bearer", ""));
        if (StringUtils.hasText(status)) {
            queryParam.put("status", status);
        }
        Map<String, Object> queryTypeMap = new HashMap<>();
        List<Map<String, Object>> queryList = new ArrayList<>();
        if (StringUtils.hasText(startDt)) {
            queryTypeMap.put("type", "gte");
            queryTypeMap.put("value", LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                               .atStartOfDay());
            queryList.add(queryTypeMap);
        }
        if (StringUtils.hasText(endDt)) {
            queryTypeMap = new HashMap<>();
            queryTypeMap.put("type", "lt");
            queryTypeMap.put("value", LocalDate.parse(endDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                               .atStartOfDay()
                                               .plusDays(1)
                                               .minusSeconds(1));
            queryList.add(queryTypeMap);
        }
        queryParam.put("user_uuid", userUuid);
        queryParam.put("regDt", queryList);

        ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
        return this.incentiveService.total(queryParam, userUuid).flatMap(v -> {
            return this.incentiveService.list(queryParam, pageable)
                                        .collectList()
                                        .flatMap(items -> Mono.just(items.stream()
                                                                         .map(this.convertUtil::toMap)
                                                                         .collect(Collectors.toList())))
                                        .flatMap(list -> {
                                            v.put("list", list);
                                            return Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                                                          .setResult(v));
                                        });
        });
    }

    @Validated(IncentiveCreate.class)
    @PostMapping
    public Mono<ResponseModel<Map<String, Object>>> create(@RequestBody @Valid final IncentiveModel incentiveModel, @RequestHeader("Authorization") String token) {

        String userUuid = jwtUtil.setJwtParser("jwt")
                                 .getLoginUuidByJwt(token.replace("Bearer", ""));
        incentiveModel.setUserUuid(userUuid);

        return this.incentiveService.create(incentiveModel).flatMap(result -> {
            ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
            return Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                          .setResult(this.convertUtil.toMap(result)));
        });
    }

    @Validated(IncentiveWithdrawal.class)
    @Transactional
    @PostMapping("withdrawal")
    public Mono<ResponseModel<Map<String, Object>>> withdrawal(@RequestBody @Valid final IncentiveModel incentiveModel, @RequestHeader("Authorization") String token) {
        ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
        if (!(incentiveModel.getIsAllChecked() instanceof Boolean)) {
            boolean isBooleanType = Boolean.parseBoolean(String.valueOf(incentiveModel.getIsAllChecked()));
            if (!isBooleanType) {
                Map<String, Object> returnMap = new HashMap<>();
                returnMap.put("message", "isAllChecked 필드는 true | false 이어야만 합니다.");
                return Mono.just(responseModel.setStatus(ResponseType.BAD_REQUEST.code())
                                              .setResult(returnMap));
            }
            incentiveModel.setIsAllChecked(isBooleanType);
        }
        String userUuid = jwtUtil.setJwtParser("jwt")
                                 .getLoginUuidByJwt(token.replace("Bearer", ""));
        incentiveModel.setUserUuid(userUuid);
        return this.incentiveService.withdrawalCreate(incentiveModel).flatMap(result -> {
            int code = (int) result.get("code");
            result.remove("code");
            return Mono.just(responseModel.setStatus(code).setResult(result));
        });
    }
}
