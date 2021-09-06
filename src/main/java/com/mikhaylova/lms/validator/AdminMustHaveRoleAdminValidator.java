package com.mikhaylova.lms.validator;

import com.mikhaylova.lms.annotation.AdminMustHaveRoleAdmin;
import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.UserDto;
import com.mikhaylova.lms.service.RoleService;
import com.mikhaylova.lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AdminMustHaveRoleAdminValidator implements ConstraintValidator<AdminMustHaveRoleAdmin, UserDto> {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext context) {
        User user = userService.findUserById(userDto.getId());
        boolean isValid = true;
        boolean isUserAdmin = user.getRoles().contains(roleService.findByName("ROLE_ADMIN"));
        if (isUserAdmin){
            boolean isUserStillAdmin = userDto.getRoles().contains(roleService.findByName("ROLE_ADMIN"));
            if (!isUserStillAdmin) isValid = false;
        }
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("roles").addConstraintViolation();
        }
        return isValid;
    }

}