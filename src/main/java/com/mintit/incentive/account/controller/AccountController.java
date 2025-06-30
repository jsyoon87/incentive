package com.mintit.incentive.account.controller;

import com.mintit.incentive.account.model.AccountModel;
import com.mintit.incentive.account.service.AccountService;
import com.mintit.incentive.common.model.ResponseModel;
import com.mintit.incentive.common.type.ResponseType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping("account")
@RequiredArgsConstructor
public class AccountController {

    final private AccountService accountService;

    @PostMapping("valid")
    public Mono<Map<String, Object>> valid(@RequestBody @Valid final AccountModel accountModel) {
        return this.accountService.transferBank(accountModel);
    }

    @GetMapping("bank-code")
    public Mono<ResponseModel<List<Map<String, Object>>>> getBankCode() {
        //TODO 보스랑 연동하여 - BANK_CD 공통 고드값 전달

        List<Map<String, Object>> bankCodeList = new ArrayList<>();
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "001");
            put("bankName", "한국은행");
            put("sortSeq", 99);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "002");
            put("bankName", "KDB산업은행");
            put("sortSeq", 1);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "003");
            put("bankName", "IBK기업은행");
            put("sortSeq", 2);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "004");
            put("bankName", "KB국민은행");
            put("sortSeq", 3);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "005");
            put("bankName", "KEB하나은행");
            put("sortSeq", 4);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "007");
            put("bankName", "SH수협은행");
            put("sortSeq", 5);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "011");
            put("bankName", "NH농협은행(농협중앙회)");
            put("sortSeq", 6);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "012");
            put("bankName", "NH농협은행(농.축협)");
            put("sortSeq", 7);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "020");
            put("bankName", "우리은행");
            put("sortSeq", 8);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "026");
            put("bankName", "한국씨티은행");
            put("sortSeq", 9);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "001");
            put("bankName", "한국은행");
            put("sortSeq", 10);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "031");
            put("bankName", "DGB대구은행");
            put("sortSeq", 11);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "032");
            put("bankName", "BNK부산은행");
            put("sortSeq", 12);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "034");
            put("bankName", "광주은행");
            put("sortSeq", 13);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "035");
            put("bankName", "제주은행");
            put("sortSeq", 14);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "037");
            put("bankName", "전북은행");
            put("sortSeq", 15);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "039");
            put("bankName", "BNK경남은행");
            put("sortSeq", 16);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "041");
            put("bankName", "우리카드");
            put("sortSeq", 17);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "044");
            put("bankName", "외환카드");
            put("sortSeq", 18);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "045");
            put("bankName", "MG새마을금고");
            put("sortSeq", 19);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "048");
            put("bankName", "신협");
            put("sortSeq", 20);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "050");
            put("bankName", "저축은행중앙회");
            put("sortSeq", 21);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "051");
            put("bankName", "기타외국계은행");
            put("sortSeq", 22);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "054");
            put("bankName", "HSBC은행");
            put("sortSeq", 23);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "055");
            put("bankName", "도이치은행");
            put("sortSeq", 24);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "057");
            put("bankName", "제이피모간체이스은행");
            put("sortSeq", 25);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "060");
            put("bankName", "BOA은행");
            put("sortSeq", 26);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "061");
            put("bankName", "비엔피파리바은행");
            put("sortSeq", 27);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "064");
            put("bankName", "산림조합중앙회");
            put("sortSeq", 28);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "071");
            put("bankName", "우체국");
            put("sortSeq", 29);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "081");
            put("bankName", "KEB하나은행(구)");
            put("sortSeq", 30);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "088");
            put("bankName", "신한은행");
            put("sortSeq", 31);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "089");
            put("bankName", "케이뱅크");
            put("sortSeq", 32);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "090");
            put("bankName", "카카오뱅크");
            put("sortSeq", 33);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "092");
            put("bankName", "토스뱅크");
            put("sortSeq", 34);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "218");
            put("bankName", "KB증권");
            put("sortSeq", 35);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "230");
            put("bankName", "미래에셋대우");
            put("sortSeq", 36);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "238");
            put("bankName", "대우증권");
            put("sortSeq", 37);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "240");
            put("bankName", "삼성증권");
            put("sortSeq", 38);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "243");
            put("bankName", "한국투자증권");
            put("sortSeq", 39);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "247");
            put("bankName", "NH투자증권");
            put("sortSeq", 40);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "261");
            put("bankName", "교보증권");
            put("sortSeq", 41);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "262");
            put("bankName", "하이투자증권");
            put("sortSeq", 42);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "263");
            put("bankName", "HMC투자증권");
            put("sortSeq", 43);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "264");
            put("bankName", "키움증권");
            put("sortSeq", 44);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "265");
            put("bankName", "이베스트투자증권");
            put("sortSeq", 45);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "266");
            put("bankName", "SK증권");
            put("sortSeq", 46);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "267");
            put("bankName", "대신증권");
            put("sortSeq", 47);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "268");
            put("bankName", "메리츠종금증권(아이엠투자증권)");
            put("sortSeq", 48);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "269");
            put("bankName", "한화투자증권");
            put("sortSeq", 49);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "270");
            put("bankName", "하나금융투자");
            put("sortSeq", 50);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "278");
            put("bankName", "신한금융투자");
            put("sortSeq", 51);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "279");
            put("bankName", "DB금융투자");
            put("sortSeq", 52);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "280");
            put("bankName", "유진투자증권");
            put("sortSeq", 53);
        }});
        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "287");
            put("bankName", "메리츠종금증권(메리츠종합금융증권)");
            put("sortSeq", 54);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "290");
            put("bankName", "부국증권");
            put("sortSeq", 55);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "291");
            put("bankName", "신영증권");
            put("sortSeq", 56);
        }});

        bankCodeList.add(new HashMap<String, Object>() {{
            put("bankCode", "292");
            put("bankName", "케이프투자증권");
            put("sortSeq", 57);
        }});

        List<Map<String, Object>> sortList = bankCodeList.stream()
                                                         .sorted(Comparator.comparingInt(o -> (int) o.get("sortSeq")))
                                                         .collect(Collectors.toList());

        ResponseModel<List<Map<String, Object>>> responseModel = new ResponseModel<>();
        return Mono.just(responseModel.setStatus(ResponseType.SUCCESS.code()).setResult(sortList));
    }
}
