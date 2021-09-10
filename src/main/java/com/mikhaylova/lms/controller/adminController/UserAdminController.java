package com.mikhaylova.lms.controller.adminController;

import com.mikhaylova.lms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/user")
public class UserAdminController {
    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String usersTable(Model model) {
        model.addAttribute("users", userService.findAllUserDto());
        model.addAttribute("activePage", "users");
        return "users-table";
    }
}
