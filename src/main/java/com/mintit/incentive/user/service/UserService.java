package com.mintit.incentive.user.service;

import com.mintit.incentive.common.util.ConvertUtil;
import com.mintit.incentive.common.util.CryptUtil;
import com.mintit.incentive.terms.model.TermsModel;
import com.mintit.incentive.terms.service.TermsService;
import com.mintit.incentive.user.entity.UserEntity;
import com.mintit.incentive.user.model.UserModel;
import com.mintit.incentive.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    final private TermsService termsService;

    final private UserRepository userRepository;

    final private ConvertUtil convertUtil;

    private final String[] convertField = {
        "userNm", "mobileNo", "birthDate", "accountNo", "password"
    };

    public Mono<String> create(UserModel userModel) {
        UserEntity userEntity = this.convertUtil.encrypt(userModel, UserEntity.class, this.convertField);
        return this.userRepository.save(userEntity).flatMap(r -> {
            if (StringUtils.hasText(userModel.getCheckTerms())) {
                Stream.of(userModel.getCheckTerms().split(",")).forEach(termsId -> {
                    TermsModel termsModel = new TermsModel();
                    termsModel.setUserUuid(r.getUserUuid());
                    termsModel.setTermsId(termsId);
                    this.termsService.choice(termsModel).subscribe();
                });
            }
            return Mono.just("정상적으로 등록 하였습니다");
        });

    }

    public Flux<UserEntity> list(Pageable pageable) {
        return this.userRepository.findAll(UserEntity.class, pageable)
                                  .flatMap(user -> Mono.just(this.convertUtil.decrypt(UserEntity.class, user, this.convertField)));
    }

    public Flux<UserEntity> list(Map<String, Object> params, Pageable pageable) {
        return this.userRepository.findAll(UserEntity.class, this.convertUtil.getQueryParams(UserEntity.class, params, "userNm", "userUuid"), pageable)
                                  .flatMap(user -> Mono.just(this.convertUtil.decrypt(UserEntity.class, user, this.convertField)));
    }

    public Mono<UserEntity> getUser(String userUuid, boolean isDecrypt) {
        return this.userRepository.findByUserUuid(userUuid)
                                  .flatMap(user -> Mono.just(isDecrypt
                                                             ? this.convertUtil.decrypt(UserEntity.class, user, this.convertField)
                                                             : user));
    }

    public Mono<Long> deleteUser(String ci) {
        return this.userRepository.deleteByCi(ci);
    }

    public Mono<UserEntity> getUserCheck(String ci) {
        return this.userRepository.findByCi(ci).flatMap(Mono::just);

    }

    public Mono<UserEntity> update(UserModel userModel) {
        return this.userRepository.findByUserUuid(userModel.getUserUuid()).flatMap(user -> {
            Map<String, Object> userMap = this.convertUtil.toMap(user, "id", "new");
            Map<String, Object> updateMap = this.convertUtil.toMap(userModel, "id", "new")
                                                            .entrySet()
                                                            .stream()
                                                            .filter(e -> !e.getValue().equals(""))
                                                            .peek(e -> {
                                                                if (Arrays.asList(convertField)
                                                                          .contains(e.getKey())) {
                                                                    try {
                                                                        String value = (String) e.getValue();
                                                                        e.setValue(CryptUtil.encryptAES256(value));
                                                                    } catch (Exception ex) {
                                                                        log.error("Convert Util Crypt Error", ex);
                                                                    }
                                                                }
                                                            })
                                                            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

            updateMap.forEach((key, value) -> userMap.merge(key, value, (v1, v2) -> v2));
            UserEntity userEntity = this.convertUtil.convertValue(userMap, UserEntity.class);
            userEntity.setRegDt(LocalDateTime.now());

            return this.userRepository.save(userEntity)
                                      .flatMap(resultUser -> Mono.just(this.convertUtil.decrypt(UserEntity.class, resultUser, this.convertField)));
        });
    }

}
//