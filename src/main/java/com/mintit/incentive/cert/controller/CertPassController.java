package com.mintit.incentive.cert.controller;

import com.mintit.incentive.cert.entity.CertPassEntity;
import com.mintit.incentive.cert.repository.CertPassRepository;
import com.mintit.incentive.common.util.CryptUtil;
import com.mintit.incentive.user.repository.UserRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cert")
@Slf4j
public class CertPassController {

    @Value("${nice.pass.siteCode}")
    private String siteCode;

    @Value("${nice.pass.sitePassword}")
    private String sitePassword;

    @Value("${nice.pass.authType}")
    private String authType;


    @Value("${nice.pass.customize}")
    private String customize;

    @Value("${nice.pass.popup}")
    private String popup;

    @Value("${nice.pass.url.success}")
    private String successURL;

    @Value("${nice.pass.url.error}")
    private String errorURL;


    final private CertPassRepository certPassRepository;


    final private UserRepository userRepository;

    @GetMapping("nice-request")
    public Mono<Rendering> niceRequest(@RequestParam(value="userUuid", required=false) String userUuid) {
        return Mono.justOrEmpty(userUuid)
                   .flatMap(id -> userRepository.findByUserUuid(id)
                                                .flatMap(findUser -> Mono.just(false))
                                                .switchIfEmpty(Mono.defer(() -> Mono.just(true))))
                   .switchIfEmpty(Mono.defer(() -> Mono.just(false)))
                   .flatMap(isCheck -> {
                       if (isCheck) {
                           return Mono.just(Rendering.view("niceRequest")
                                                     .modelAttribute("error", true)
                                                     .modelAttribute("message", "등록된 아이디 정보가 없습니다")
                                                     .build());
                       }
                       NiceID.Check.CPClient niceCheck = new NiceID.Check.CPClient();
                       String requestNumber = niceCheck.getRequestNO(siteCode);

                       String plainData = "7:REQ_SEQ" + requestNumber.getBytes().length + ":" + requestNumber + "8:SITECODE" + siteCode.getBytes().length + ":" + siteCode + "9:AUTH_TYPE" + authType.getBytes().length + ":" + authType + "7:RTN_URL" + successURL.getBytes().length + ":" + successURL + "7:ERR_URL" + errorURL.getBytes().length + ":" + errorURL + "11:POPUP_GUBUN" + popup.getBytes().length + ":" + popup + "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize + "6:GENDER" + "".getBytes().length + ":" + "";

                       int iReturn = niceCheck.fnEncode(siteCode, sitePassword, plainData);

                       String message = "";
                       String encData = "";
                       boolean isError = true;

                       if (iReturn == 0) {
                           encData = niceCheck.getCipherData();
                           isError = false;
                       } else if (iReturn == -1) {
                           message = "암호화 시스템 에러입니다.";
                       } else if (iReturn == -2) {
                           message = "암호화 처리오류입니다.";
                       } else if (iReturn == -3) {
                           message = "암호화 데이터 오류입니다.";
                       } else if (iReturn == -9) {
                           message = "입력 데이터 오류입니다.";
                       } else {
                           message = "알수 없는 에러 입니다. iReturn : " + iReturn;
                       }

                       return Mono.just(Rendering.view("niceRequest")
                                                 .modelAttribute("encData", encData)
                                                 .modelAttribute("message", message)
                                                 .modelAttribute("error", isError)
                                                 .build());

                   });

    }

    @PostMapping(value="nice-success")
    public Mono<Rendering> success(ServerWebExchange serverWebExchange) {
        return serverWebExchange.getFormData().flatMap(data -> {
            NiceID.Check.CPClient niceCheck = new NiceID.Check.CPClient();
            String EncodeData = data.getFirst("EncodeData");
            int isReturn = niceCheck.fnDecode(siteCode, sitePassword, EncodeData);
            Map<Object, Object> convert = new HashMap<>();
            HashMap<?, ?> result = null;
            CertPassEntity certPassEntity = new CertPassEntity();
            try {
                String errorMessage = "";

                List<String> resultKeys = Arrays.asList("MOBILE_NO", "MOBILE_CO", "CI", "GENDER", "BIRTHDATE", "NAME");
                convert.put("isError", true);

                if (isReturn == 0) {
                    String planData = niceCheck.getPlainData();
                    result = niceCheck.fnParse(planData);
                    certPassEntity.setReqSeq((String) result.get("REQ_SEQ"));
                    certPassEntity.setMobileNo(CryptUtil.encryptAES256((String) result.get("MOBILE_NO")));
                    certPassEntity.setMobileCo(CryptUtil.encryptAES256((String) result.get("MOBILE_CO")));
                    certPassEntity.setGender((String) result.get("GENDER"));
                    certPassEntity.setBirthdate(CryptUtil.encryptAES256((String) result.get("BIRTHDATE")));
                    certPassEntity.setNationalinfo((String) result.get("NATIONALINFO"));
                    certPassEntity.setAuthType((String) result.get("AUTH_TYPE"));
                    certPassEntity.setCi((String) result.get("CI"));
                    certPassEntity.setName(CryptUtil.encryptAES256((String) result.get("NAME")));

                    convert = result.entrySet()
                                    .stream()
                                    .filter(e -> resultKeys.contains(e.getKey()))
                                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
                    convert.put("isError", false);
                } else if (isReturn == -1) {
                    errorMessage = "복호화 시스템 에러입니다.";
                } else if (isReturn == -4) {
                    errorMessage = "복호화 처리오류입니다.";
                } else if (isReturn == -5) {
                    errorMessage = "복호화 해쉬 오류입니다.";
                } else if (isReturn == -6) {
                    errorMessage = "복호화 데이터 오류입니다.";
                } else if (isReturn == -9) {
                    errorMessage = "입력 데이터 오류입니다.";
                } else if (isReturn == -12) {
                    errorMessage = "사이트 패스워드 오류입니다.";
                } else {
                    errorMessage = "알수 없는 에러 입니다. iReturn : " + isReturn;
                }

                certPassEntity.setMessage(errorMessage);
                convert.put("errorMessage", errorMessage);
                //this.certPassRepository.save(certPassEntity).subscribe();
            } catch (Exception err) {
                convert.put("errorMessage", err.getMessage());
            }

            if (!(boolean) convert.get("isError")) {
                Map<Object, Object> finalConvert = convert;
                assert result != null;
                return this.certPassRepository.findByReqSeq((String) result.get("REQ_SEQ"))
                                              .flatMap(v -> {
                                                  for (Entry<Object, Object> en : finalConvert.entrySet()) {
                                                      en.setValue("");
                                                  }
                                                  finalConvert.put("isError", true);
                                                  finalConvert.put("errorMessage", "이미 인증받은 데이터 입니다");
                                                  return Mono.just(Rendering.view("niceResponse")
                                                                            .modelAttribute("result", finalConvert)
                                                                            .build());
                                              })
                                              .switchIfEmpty(Mono.defer(() -> {
                                                  this.certPassRepository.save(certPassEntity)
                                                                         .subscribe();
                                                  return Mono.just(Rendering.view("niceResponse")
                                                                            .modelAttribute("result", finalConvert)
                                                                            .build());
                                              }));

            }

            return Mono.just(Rendering.view("niceResponse")
                                      .modelAttribute("result", convert)
                                      .build());
        });

    }

    @GetMapping("nice-error")
    public Mono<Rendering> error(@RequestParam String EncodeData) {

        NiceID.Check.CPClient niceCheck = new NiceID.Check.CPClient();
        int isReturn = niceCheck.fnDecode(siteCode, sitePassword, EncodeData);

        String errorMessage = "";
        Map<Object, Object> convert = new HashMap<>();
        convert.put("isError", true);
        List<String> resultKeys = Arrays.asList("REQ_SEQ", "ERR_CODE", "AUTH_TYPE");
        CertPassEntity certPassEntity = new CertPassEntity();
        if (isReturn == 0) {
            String planData = niceCheck.getPlainData();
            HashMap<?, ?> result = niceCheck.fnParse(planData);

            certPassEntity.setReqSeq((String) result.get("REQ_SEQ"));
            certPassEntity.setErrCode((String) result.get("ERR_CODE"));
            certPassEntity.setAuthType((String) result.get("AUTH_TYPE"));

            convert = result.entrySet()
                            .stream()
                            .filter(e -> resultKeys.contains(e.getKey()))
                            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        } else if (isReturn == -1) {
            errorMessage = "복호화 시스템 에러입니다.";
        } else if (isReturn == -4) {
            errorMessage = "복호화 처리오류입니다.";
        } else if (isReturn == -5) {
            errorMessage = "복호화 해쉬 오류입니다.";
        } else if (isReturn == -6) {
            errorMessage = "복호화 데이터 오류입니다.";
        } else if (isReturn == -9) {
            errorMessage = "입력 데이터 오류입니다.";
        } else if (isReturn == -12) {
            errorMessage = "사이트 패스워드 오류입니다.";
        } else {
            errorMessage = "알수 없는 에러 입니다. iReturn : " + isReturn;
        }
        certPassEntity.setMessage(errorMessage);
        convert.put("errorMessage", errorMessage);
        this.certPassRepository.save(certPassEntity).subscribe();

        return Mono.just(Rendering.view("niceResponse").modelAttribute("result", convert).build());
    }

}
