package com.mintit.incentive.user.controller;

import com.mintit.incentive.common.model.ResponseModel;
import com.mintit.incentive.common.type.ResponseType;
import com.mintit.incentive.common.util.ConvertUtil;
import com.mintit.incentive.user.model.UserCreate;
import com.mintit.incentive.user.model.UserModel;
import com.mintit.incentive.user.model.UserUpdate;
import com.mintit.incentive.user.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    final private UserService userService;

    final private ConvertUtil convertUtil;

    @GetMapping()
    public Mono<ResponseModel<List<Map<String, Object>>>> list(@RequestParam Map<String, Object> params, Pageable pageable) {
        ResponseModel<List<Map<String, Object>>> responseModel = new ResponseModel<>();
        return this.userService.list(params, pageable)
                               .collectList()
                               .flatMap(items -> Mono.just(items.stream()
                                                                .map(this.convertUtil::toMap)
                                                                .collect(Collectors.toList())))
                               .flatMap(users -> Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                                                        .setResult(users)));

    }

    @GetMapping("{userUuid}")
    public Mono<ResponseModel<Object>> getUser(@PathVariable("userUuid") String userUuid) {
        ResponseModel<Object> responseModel = new ResponseModel<>();
        return this.userService.getUser(userUuid, true)
                               .flatMap(user -> Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                                                       .setResult(this.convertUtil.toMap(user))))
                               .switchIfEmpty(Mono.defer(() -> {
                                   Map<String, String> resultMap = new HashMap<>();
                                   resultMap.put("message", "유저 정보를 찾을수 없습니다");
                                   return Mono.just(responseModel.setStatus(ResponseType.NOT_LOGIN.code())
                                                                 .setResult(resultMap));
                               }));
    }

    @Transactional
    @Validated(UserCreate.class)
    @PostMapping()
    public Mono<ResponseModel<Map<String, Object>>> create(@RequestBody @Valid final UserModel userModel) {
        return this.userService.create(userModel).flatMap(message -> {
            ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
            Map<String, Object> result = new HashMap<>();
            result.put("message", message);

            return Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                          .setResult(result));
        });
    }

    @Validated(UserUpdate.class)
    @PutMapping()
    public Mono<ResponseModel<Map<String, Object>>> update(@RequestBody @Valid final UserModel userModel) {
        ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
        return this.userService.update(userModel)
                               .flatMap(user -> Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                                                       .setResult(this.convertUtil.toMap(user))));
    }
}
