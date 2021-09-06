package com.mikhaylova.lms.validator;

import com.mikhaylova.lms.annotation.UniqueEmailOrNotChanged;
import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.UserDto;
import com.mikhaylova.lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailOrNotChangedValidator implements ConstraintValidator<UniqueEmailOrNotChanged, UserDto> {
    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext context) {
        boolean isValid = !userService.existsByEmail(userDto.getEmail());
        if (!isValid) {
            User user = userService.findUserById(userDto.getId());
            if (user.getEmail()==null || user.getEmail().equals(userDto.getEmail()))
                isValid = true;
            else {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode("email").addConstraintViolation();
            }

        }
        return isValid;
    }
}