package com.mintit.incentive.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@NoArgsConstructor
@Accessors(chain=true)
public class ResponseModel<T> {

    /*
     * 결과 코드
     */

    int status;

    /*
     * Data
     */
    @JsonInclude(Include.NON_NULL)
    T result;
}
