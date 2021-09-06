package com.mikhaylova.lms.annotation;

import com.mikhaylova.lms.validator.AdminMustHaveRoleAdminValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = AdminMustHaveRoleAdminValidator.class)
public @interface AdminMustHaveRoleAdmin {
    String message() default "Админ не может перестать быть админом...";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
