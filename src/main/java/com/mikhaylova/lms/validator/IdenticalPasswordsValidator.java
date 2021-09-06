package com.mikhaylova.lms.validator;

import com.mikhaylova.lms.annotation.IdenticalPasswords;
import com.mikhaylova.lms.dto.RegisterUserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdenticalPasswordsValidator implements ConstraintValidator<IdenticalPasswords, RegisterUserDto> {
    @Override
    public boolean isValid(RegisterUserDto registerUserDto, ConstraintValidatorContext context) {
        boolean isValid = registerUserDto.getPassword().equals(registerUserDto.getRepeatedPassword());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("repeatedPassword").addConstraintViolation();
        }
        return isValid;
    }

}
