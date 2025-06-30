package com.mintit.incentive.auth.controller;

import com.mintit.incentive.auth.model.AuthCiValid;
import com.mintit.incentive.auth.model.AuthLogin;
import com.mintit.incentive.auth.model.AuthModel;
import com.mintit.incentive.auth.model.AuthPassword;
import com.mintit.incentive.auth.model.AuthRefreshToken;
import com.mintit.incentive.auth.service.AuthService;
import com.mintit.incentive.common.model.ResponseModel;
import com.mintit.incentive.common.type.ResponseType;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Value("${spring.env}")
    private String env;
    final private AuthService authService;

    @Validated(AuthLogin.class)
    @PostMapping("login")
    public Mono<ResponseModel<Map<String, Object>>> login(@RequestBody @Valid final AuthModel authModel) {
        return this.authService.login(authModel).flatMap(result -> {
            ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
            int returnCode = (boolean) result.get("isLogin")
                             ? ResponseType.SUCCESS.code()
                             : ResponseType.LOGIN_FAIL.code();
            result.remove("isLogin");
            return Mono.just(responseModel.setStatus(returnCode).setResult(result));
        });
    }

    @Validated(AuthRefreshToken.class)
    @PostMapping("auto-login")
    public Mono<ResponseModel<Map<String, Object>>> autoLogin(@RequestBody @Valid final AuthModel authModel) {
        return this.authService.refresh(authModel, true).flatMap(result -> {
            ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
            int code = (int) result.get("code");
            result.remove("code");
            return Mono.just(responseModel.setStatus(code).setResult(result));
        });
    }

    @Validated(AuthRefreshToken.class)
    @PostMapping("refresh-token")
    public Mono<ResponseModel<Map<String, Object>>> refreshToken(@RequestBody @Valid final AuthModel authModel) {
        return this.authService.refresh(authModel, false).flatMap(result -> {
            ResponseModel<Map<String, Object>> responseModel = new ResponseModel<>();
            int code = (int) result.get("code");
            result.remove("code");
            return Mono.just(responseModel.setStatus(code).setResult(result));
        });
    }

    @Validated(AuthCiValid.class)
    @PostMapping("user-valid")
    public Mono<ResponseModel<Object>> getUserCheck(@RequestBody @Valid final AuthModel authModel) {
        ResponseModel<Object> responseModel = new ResponseModel<>();
        return this.authService.getUserCheck(authModel.getCi())
                               .flatMap(result -> {
                                   return Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code())
                                                                 .setResult(result));
                               })
                               .switchIfEmpty(Mono.defer(() -> Mono.just(responseModel.setStatus(ResponseType.NOT_FOUND_USER.code())
                                                                                      .setResult(new HashMap<String, Object>() {{
                                                                                          put("message", "고객 정보를 찾을수 없습니다");
                                                                                      }}))));
    }

    @Profile({"local"})
    @DeleteMapping("test-user-delete")
    public Mono<String> delete(@RequestBody @Valid final AuthModel authModel) {
        if (!StringUtils.hasText(env)) {
            return Mono.just("사용할수 없는 환경 입니다.");
        }

        if (!env.equalsIgnoreCase("dev") && !env.equalsIgnoreCase("local")) {
            return Mono.just("사용할수 없는 환경 입니다.");
        }

        ResponseModel<Object> responseModel = new ResponseModel<>();
        return this.authService.deleteUser(authModel.getCi())
                               .flatMap(v -> v == 0
                                             ? Mono.just("유저 정보 없음")
                                             : Mono.just("삭제완료 - 테스트API"))
                               .switchIfEmpty(Mono.defer(() -> Mono.just("유저 정보 없음")));

    }

    @Validated(AuthPassword.class)
    @PutMapping("change-password")
    public Mono<ResponseModel<String>> changePassword(@RequestBody @Valid final AuthModel authModel) {
        ResponseModel<String> responseModel = new ResponseModel<>();
        return this.authService.changePassword(authModel)
                               .flatMap(result -> Mono.just(responseModel.setStatus((int) result.get("code"))
                                                                         .setResult((String) result.get("message"))));

    }
}
