package com.mintit.incentive.notice.controller;

import com.mintit.incentive.common.model.ResponseModel;
import com.mintit.incentive.common.type.ResponseType;
import com.mintit.incentive.common.util.ConvertUtil;
import com.mintit.incentive.common.util.JWTUtil;
import com.mintit.incentive.notice.model.NoticeCreate;
import com.mintit.incentive.notice.model.NoticeModel;
import com.mintit.incentive.notice.service.NoticeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
@RequestMapping("notices")
public class NoticeController {

    final private NoticeService noticeService;

    final private JWTUtil jwtUtil;

    final private ConvertUtil convertUtil;

    @GetMapping()
    public Mono<ResponseModel<Map<String, Object>>> list(@RequestParam(value="noticeType", required=false) String noticeType, @RequestParam(value="userGroup", required=false) String userGroup, Pageable pageable) {
        LinkedHashMap<String, Object> queryParam = new LinkedHashMap<>();

        if (StringUtils.hasText(noticeType)) {
            queryParam.put("noticeType", noticeType);
        }
        if (StringUtils.hasText(userGroup)) {
            Map<String, Object> queryTypeMap = new HashMap<>();
            List<Map<String, Object>> queryList = new ArrayList<>();
            queryTypeMap.put("type", "in");
            queryTypeMap.put("value", Arrays.asList("MINTIT", userGroup));
            queryList.add(queryTypeMap);
            queryParam.put("userGroup", queryList);
        }

        ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();

        return this.noticeService.getPageList(queryParam, pageable)
                                 .flatMap(result -> Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                                                           .setResult(result)));

    }

    @Validated(NoticeCreate.class)
    @PostMapping()
    public Mono<ResponseModel<Map<String, Object>>> create(@RequestBody @Valid final NoticeModel noticeModel, @RequestHeader("Authorization") String token) {
        String regId = jwtUtil.setJwtParser("jwt").getLoginUuidByJwt(token.replace("Bearer", ""));
        noticeModel.setRegId(regId);
        return this.noticeService.create(noticeModel).flatMap(notice -> {
            ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
            return Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                          .setResult(this.convertUtil.toMap(notice)));
        });
    }
}
