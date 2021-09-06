package com.mikhaylova.lms.annotation;

import com.mikhaylova.lms.validator.UniqueUsernameOrNotChangedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = UniqueUsernameOrNotChangedValidator.class)
public @interface UniqueUsernameOrNotChanged {
    String message() default "Имя пользователя уже используется";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
