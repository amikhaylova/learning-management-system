package com.mikhaylova.lms.annotation;

import com.mikhaylova.lms.validator.UniqueEmailOrNotChangedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = UniqueEmailOrNotChangedValidator.class)
public @interface UniqueEmailOrNotChanged {
    String message() default "Email уже используется";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
