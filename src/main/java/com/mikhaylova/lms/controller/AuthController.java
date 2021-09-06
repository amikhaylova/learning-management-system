package com.mikhaylova.lms.controller;

import com.mikhaylova.lms.dto.RegisterUserDto;
import com.mikhaylova.lms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login-form";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new RegisterUserDto());
        return "register-form";
    }

    @PostMapping("/register")
    public String submitRegisterForm(@Valid @ModelAttribute("user") RegisterUserDto registerUserDto,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register-form";
        }
        userService.saveRegisterUserDto(registerUserDto);
        return "redirect:/login";
    }
}
