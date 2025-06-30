package com.mintit.incentive.common.annotation.impl;

import com.mintit.incentive.common.annotation.DateValid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateValidator implements ConstraintValidator<DateValid, String> {

    private String formatter;

    @Override
    public void initialize(DateValid constraintAnnotation) {
        this.formatter = constraintAnnotation.formatter();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.length() == 0) {
            return true;
        }
        try {
            LocalDate.from(LocalDate.parse(value, DateTimeFormatter.ofPattern(formatter)));
        } catch (DateTimeParseException e) {
            log.error("Date Validator - ", e);
            return false;
        }
        return true;
    }
}


