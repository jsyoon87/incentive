package com.mintit.incentive.common.type;

public enum ResponseType {
    SUCCESS("200"), //성공
    NOT_LOGIN("801"),//로인 정보 없음

    LOGIN_FAIL("802"), //로그인 실패
    UNAUTHORIZATION("803"), //권한 오류

    BAD_REQUEST("804"), //잘못된 파라미터

    FAIL("805"), //실패

    AUTH_FAIL("806"), //인증정보 오류

    NOT_FOUND_USER("807"),  //유저정보 없음

    NOT_FOUND("808"),  // 오류 - 패스워드, 이메일 인증시 실패하였을때

    NOT_FOUND_API("809"), //등록되지 않은 API 호출

    USER_EXPIRATION("810"),

    ERROR("599"); //알수 없는 에러 - 서버에러

    int code;

    ResponseType(String code) {
        this.code = Integer.parseInt(code);
    }

    public int code() {
        return this.code;
    }

}
