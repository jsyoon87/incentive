package com.mintit.incentive.auth.service;

import com.mintit.incentive.auth.model.AuthModel;
import com.mintit.incentive.auth.repository.AuthHistoryRepository;
import com.mintit.incentive.common.type.ResponseType;
import com.mintit.incentive.common.util.ConvertUtil;
import com.mintit.incentive.common.util.JWTUtil;
import com.mintit.incentive.user.entity.UserEntity;
import com.mintit.incentive.user.entity.UserTokenEntity;
import com.mintit.incentive.user.model.UserModel;
import com.mintit.incentive.user.repository.UserRepository;
import com.mintit.incentive.user.repository.UserTokenRepository;
import com.mintit.incentive.user.service.UserService;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;

    private final UserRepository userRepository;

    private final AuthHistoryRepository authHistoryRepository;

    private final ConvertUtil convertUtil;

    private final UserTokenRepository userTokenRepository;

    private final String[] convertField = {
        "userNm", "mobileNo", "birthDate", "accountNo", "password"
    };


    private final UserService userService;

    public Mono<Map<String, Object>> login(AuthModel authModel) {
        if (authModel.getCi() == null) {
            return Mono.error(new ServerWebInputException("필수값이 누락 되였습니다"));
        }

        return userRepository.findByCi(authModel.getCi()).flatMap(user -> {
            Map<String, Object> userMap = new HashMap<>();
            UserEntity decrypt = this.convertUtil.decrypt(UserEntity.class, user, this.convertField);
            userMap.put("isLogin", true);
            userMap.put("user", this.convertUtil.toMap(decrypt));
            userMap.put("accessToken", jwtUtil.generateJwtToken(user));
            userMap.put("refreshToken", jwtUtil.setJwtParser("refresh")
                                               .generateRefreshJwtToken(user));
            return Mono.just(userMap);
        }).doOnNext((userMap) -> {
            if ((boolean) userMap.get("isLogin")) {
                UserTokenEntity userTokenEntity = new UserTokenEntity();
                Map<?, ?> getUserMap = (Map<?, ?>) userMap.get("user");
                userTokenEntity.setUserUuid((String) getUserMap.get("userUuid"));
                userTokenEntity.setRefreshToken((String) userMap.get("refreshToken"));
                userTokenRepository.findByUserUuid((String) getUserMap.get("userUuid"))
                                   .flatMap(userToken -> {
                                       userTokenEntity.setLastDt(LocalDateTime.now());
                                       return userTokenRepository.save(userTokenEntity);
                                   })
                                   .switchIfEmpty(userTokenRepository.save(userTokenEntity))
                                   .subscribe();
            }
        }).switchIfEmpty(Mono.defer(() -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("isLogin", false);
            userMap.put("message", "유저 정보가 존재하지 않습니다");
            return Mono.just(userMap);
        }));
    }

    public Mono<Object> getUserCheck(String ci) {
        return this.userService.getUserCheck(ci).flatMap(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userUuid", user.getUserUuid());
            return Mono.just(map);
        });
    }

    public Mono<Long> deleteUser(String ci) {
        return this.userService.deleteUser(ci);
    }

    public Mono<Map<String, Object>> changePassword(AuthModel authModel) {
        Map<String, Object> resultMap = new HashMap<>();
        return this.authHistoryRepository.selectAuthHistory(authModel.getAuthKey())
                                         .flatMap(result -> {
                                             UserModel userModel = new UserModel();
                                             userModel.setUserUuid(authModel.getUserUuid());
                                             return this.userService.update(userModel)
                                                                    .flatMap(v -> {
                                                                        resultMap.put("code", ResponseType.SUCCESS.code());
                                                                        resultMap.put("message", "success");
                                                                        //상태변경
                                                                        result.setStatus("1");
                                                                        this.authHistoryRepository.save(result)
                                                                                                  .subscribe();
                                                                        return Mono.just(resultMap);
                                                                    })
                                                                    .switchIfEmpty(Mono.defer(() -> {
                                                                        resultMap.put("code", ResponseType.NOT_FOUND_USER.code());
                                                                        resultMap.put("message", "고객 정보를 찾을수 없습니다");
                                                                        return Mono.just(resultMap);
                                                                    }));
                                         })
                                         .switchIfEmpty(Mono.defer(() -> {
                                             resultMap.put("code", ResponseType.AUTH_FAIL.code());
                                             resultMap.put("message", "유효하지 않은 인증 정보입니다");
                                             return Mono.just(resultMap);
                                         }));
    }

    public Mono<Map<String, Object>> refresh(AuthModel authModel, boolean auto) {

        Claims jwtUser = jwtUtil.setJwtParser("refresh").getLoginByJwt(authModel.getRefreshToken());
        UserEntity userEntity = new UserEntity();
        userEntity.setUserNm((String) jwtUser.get("userNm"));
        userEntity.setUserUuid((String) jwtUser.get("userUuid"));

        return userTokenRepository.findByUserUuidAndRefreshToken(userEntity.getUserUuid(), authModel.getRefreshToken())
                                  .flatMap(userToken -> {
                                      Map<String, Object> userMap = new HashMap<>();
                                      if (auto) {
                                          return this.userRepository.findByUserUuid(userEntity.getUserUuid())
                                                                    .flatMap(user -> {
                                                                        UserEntity decrypt = this.convertUtil.decrypt(UserEntity.class, user, this.convertField);
                                                                        userMap.put("code", ResponseType.SUCCESS.code());
                                                                        userMap.put("user", this.convertUtil.toMap(decrypt));
                                                                        userMap.put("accessToken", jwtUtil.setJwtParser("jwt")
                                                                                                          .generateJwtToken(userEntity));
                                                                        userMap.put("refreshToken", authModel.getRefreshToken());
                                                                        return Mono.just(userMap);

                                                                    });
                                      }
                                      userMap.put("code", ResponseType.SUCCESS.code());
                                      userMap.put("refreshToken", authModel.getRefreshToken());
                                      userMap.put("accessToken", jwtUtil.setJwtParser("jwt")
                                                                        .generateJwtToken(userEntity));

                                      return Mono.just(userMap);
                                  })
                                  .switchIfEmpty(Mono.defer(() -> {
                                      Map<String, Object> userMap = new HashMap<>();
                                      userMap.put("code", ResponseType.USER_EXPIRATION.code());
                                      userMap.put("message", "유효하지 않은 토큰 정보입니다 - Refresh");
                                      return Mono.just(userMap);
                                  }));

    }

}
