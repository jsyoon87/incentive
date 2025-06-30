package com.mintit.incentive.incentive.service;

import com.mintit.incentive.common.exception.CustomException;
import com.mintit.incentive.common.type.ResponseType;
import com.mintit.incentive.common.util.Constants.INCENTIVE_AMOUNT_TYPE;
import com.mintit.incentive.common.util.Constants.INCENTIVE_WITHDRAWAL_TYPE;
import com.mintit.incentive.common.util.ConvertUtil;
import com.mintit.incentive.incentive.entity.IncentiveEntity;
import com.mintit.incentive.incentive.entity.IncentiveWithdrawalEntity;
import com.mintit.incentive.incentive.model.IncentiveModel;
import com.mintit.incentive.incentive.repository.IncentiveRepository;
import com.mintit.incentive.incentive.repository.IncentiveWithdrawalRepository;
import com.mintit.incentive.user.service.UserService;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class IncentiveService {


    private final IncentiveRepository incentiveRepository;

    private final IncentiveWithdrawalRepository incentiveWithdrawalRepository;

    private final UserService userService;

    final private ConvertUtil convertUtil;

    public Mono<IncentiveEntity> create(IncentiveModel incentiveModel) {
        IncentiveEntity incentive = this.convertUtil.convert(incentiveModel, IncentiveEntity.class);
        incentive.setStatus(INCENTIVE_AMOUNT_TYPE.APPLY.value());
        return this.incentiveRepository.save(incentive)
                                       .flatMap(result -> this.incentiveRepository.findByIncentiveId(result.getId()));
    }

    public Flux<IncentiveEntity> list(LinkedHashMap<String, Object> queryParam, Pageable pageable) {
        return this.incentiveRepository.findAll(IncentiveEntity.class, queryParam, pageable);
    }

    public Mono<Map<String, Object>> withdrawalCreate(IncentiveModel incentiveModel) {
        LinkedHashMap<String, Object> queryParam = new LinkedHashMap<>();
        Map<String, Object> queryTypeMap = new HashMap<>();
        queryTypeMap.put("type", (boolean) incentiveModel.getIsAllChecked() ? "notin" : "in");
        queryTypeMap.put("value", incentiveModel.getList());

        queryParam.put("user_uuid", incentiveModel.getUserUuid());
        queryParam.put("status", INCENTIVE_AMOUNT_TYPE.POSSIBLE.value());
        queryParam.put("incentive_id", queryTypeMap);

        return this.userService.getUser(incentiveModel.getUserUuid(), false)
                               .flatMap(user -> this.incentiveRepository.findAll(IncentiveEntity.class, queryParam)
                                                                        .flatMap(incentive -> this.incentiveWithdrawalRepository.findByIncentiveId(incentive.getIncentiveId())
                                                                                                                                .flatMap(v -> {
                                                                                                                                    log.error("중복된 출금 요청 - {}", incentive.getIncentiveId());
                                                                                                                                    return Mono.error(new CustomException("중복된 출금 요청으로 실패 하였습니다"));
                                                                                                                                })
                                                                                                                                .switchIfEmpty(Mono.defer(() -> {
                                                                                                                                    IncentiveWithdrawalEntity incentiveWithdrawalEntity = new IncentiveWithdrawalEntity();
                                                                                                                                    incentiveWithdrawalEntity.setIncentiveId(incentive.getIncentiveId());
                                                                                                                                    incentiveWithdrawalEntity.setAmount(incentive.getAmount());
                                                                                                                                    incentiveWithdrawalEntity.setAccountNo(user.getAccountNo());
                                                                                                                                    incentiveWithdrawalEntity.setBankCd(user.getBankCd());
                                                                                                                                    incentiveWithdrawalEntity.setStatus(INCENTIVE_WITHDRAWAL_TYPE.APPLY.value());
                                                                                                                                    incentiveWithdrawalEntity.setRegId(incentive.getUserUuid());
                                                                                                                                    return this.incentiveWithdrawalRepository.save(incentiveWithdrawalEntity);
                                                                                                                                })))
                                                                        .collectList()
                                                                        .flatMap(v -> {
                                                                            Map<String, Object> resultMap = new HashMap<>();
                                                                            resultMap.put("code", v.size() == 0
                                                                                                  ? ResponseType.FAIL.code()
                                                                                                  : ResponseType.SUCCESS.code());
                                                                            resultMap.put("message",
                                                                                v.size() == 0
                                                                                ? "출금 정보가 존재하지 않습니다"
                                                                                : "출금 요청이 정상적으로 신청 되었습니다");
                                                                            return Mono.just(resultMap);
                                                                        })

                               )
                               .switchIfEmpty(Mono.defer(() -> {
                                   Map<String, Object> resultMap = new HashMap<>();
                                   resultMap.put("code", ResponseType.NOT_FOUND_USER.code());
                                   resultMap.put("message", "잘못된 사용자 정보입니다");
                                   return Mono.just(resultMap);
                               }));

    }


    public Mono<Map<String, Object>> total(LinkedHashMap<String, Object> queryParam, String userUuid) {
        Mono<Long> total = this.incentiveRepository.totalCount(IncentiveEntity.class, queryParam);
        Mono<Long> withdrawableAmount = this.incentiveRepository.selectTotalAmount(userUuid, INCENTIVE_AMOUNT_TYPE.POSSIBLE.value());
        Mono<Long> accumulateAmount = this.incentiveRepository.selectTotalAmount(userUuid, INCENTIVE_AMOUNT_TYPE.COMPLETE.value());
        Mono<Long> totalWithdrawable = this.incentiveRepository.selectTotalCount(userUuid, INCENTIVE_AMOUNT_TYPE.POSSIBLE.value());
        Map<String, Object> resultMap = new HashMap<>();
        return total.flatMap(v -> {
            resultMap.put("totalCount", v);
            return withdrawableAmount.flatMap(v1 -> {
                resultMap.put("withdrawableAmount", v1);
                return accumulateAmount.flatMap(v2 -> {
                    resultMap.put("accumulateAmount", v2);
                    return totalWithdrawable.flatMap(v3 -> {
                        resultMap.put("totalAmount", v1 + v2);
                        resultMap.put("totalWithdrawable", v3);
                        return Mono.just(resultMap);
                    });
                });
            });
        });

    }
}
