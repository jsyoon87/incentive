package com.mintit.incentive.common.exception;

import com.mintit.incentive.common.type.ResponseType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebInputException;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        Throwable error = this.getError(request);
        String message = "";
        log.error("error - ", error);
        if (error instanceof UnauthorizationException) {
            message = Optional.ofNullable(error.getMessage()).orElse("권한이 없습니다");
            errorMap.put("status", ResponseType.UNAUTHORIZATION.code());
        } else if (error instanceof NotLoginException) {
            message = Optional.ofNullable(error.getMessage()).orElse("로그인 정보가 없습니다");
            errorMap.put("status", ResponseType.NOT_LOGIN.code());
        } else if (error instanceof ExpiredJwtException || error instanceof SignatureException || error instanceof MalformedJwtException || error instanceof UnsupportedJwtException) {
            message = "유효하지 않는 토큰 정보 입니다";
            errorMap.put("status", ResponseType.UNAUTHORIZATION.code());
        } else if (error instanceof ConstraintViolationException) {
            List<String> result = ((ConstraintViolationException) error).getConstraintViolations()
                                                                        .stream()
                                                                        .map(v -> ((PathImpl) v.getPropertyPath()).getLeafNode()
                                                                                                                  .getName() + ":" + v.getMessage())
                                                                        .collect(Collectors.toList());
            message = String.join(",", result);
            errorMap.put("status", ResponseType.BAD_REQUEST.code());
        } else if (error instanceof WebExchangeBindException) {
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Object>> validate = validator.validate(((WebExchangeBindException) error).getTarget());
            List<String> result = validate.stream()
                                          .map(v -> ((PathImpl) v.getPropertyPath()).getLeafNode()
                                                                                    .getName() + ":" + v.getMessage())
                                          .collect(Collectors.toList());
            message = String.join(",", result);
            errorMap.put("status", ResponseType.BAD_REQUEST.code());

        } else if (error instanceof ServerWebInputException) {
            message = "잘못된 파라미터 정보입니다";
            errorMap.put("status", ResponseType.BAD_REQUEST.code());
        } else if (error instanceof CustomException) {
            message = error.getMessage();
            errorMap.put("status", ResponseType.FAIL.code());
        } else {
            message = error.getMessage().equals("404 NOT_FOUND") ? "잘못된 접근 입니다" : "관리자에게 문의하세요";
            errorMap.put("status", error.getMessage().equals("404 NOT_FOUND")
                                   ? ResponseType.NOT_FOUND_API.code()
                                   : ResponseType.ERROR.code());
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("message", message);
        errorMap.put("result", resultMap);
        return errorMap;
    }
}
