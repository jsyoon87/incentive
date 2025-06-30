package com.mintit.incentive.common.util;

public class Constants {

    //인센티브 관련 코드 정의
    public enum INCENTIVE_AMOUNT_TYPE {

        APPLY("0001"), //신청
        POSSIBLE("0002"), //출금신청 가능

        EXPIRATION("0003"), //기간 만료
        COMPLETE("9999"); //지급완료

        private final String value;

        INCENTIVE_AMOUNT_TYPE(String value) {this.value = value;}

        public String value() {
            return this.value;
        }
    }


    public enum NOTICE_TYPE {
        POLICY("0001"),

        FAQ("0002");
        private final String value;

        NOTICE_TYPE(String value) {this.value = value;}

        public String value() {
            return this.value;
        }
    }


    //출금 신청 상태관리
    public enum INCENTIVE_WITHDRAWAL_TYPE {
        APPLY("0010"), //신청

        BATCH("0011"), //출금중

        ERROR("9998"), //오류

        COMPLETE("9999"); //지급완료
        private final String value;

        INCENTIVE_WITHDRAWAL_TYPE(String value) {this.value = value;}

        public String value() {
            return this.value;
        }
    }
}
