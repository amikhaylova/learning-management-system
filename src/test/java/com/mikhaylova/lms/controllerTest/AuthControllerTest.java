package com.mikhaylova.lms.controllerTest;

import com.mikhaylova.lms.controller.AuthController;
import com.mikhaylova.lms.dto.RegisterUserDto;
import com.mikhaylova.lms.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.instanceOf;

@WebMvcTest({AuthController.class})
class AuthControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void checkSubmitRegisterFormWithNotSamePasswords() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .flashAttr("user", new RegisterUserDto("test", "test", "test1")))
                .andExpect(model().attributeHasFieldErrors("user", "repeatedPassword"))
                .andExpect(view().name("register-form"));
    }

    @Test
    void checkSubmitRegisterFormWithEmptyFields() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .flashAttr("user", new RegisterUserDto("", "", "")))
                .andExpect(model().attributeHasFieldErrors("user", "repeatedPassword", "password", "username"))
                .andExpect(view().name("register-form"));
    }

    @Test
    void checkSuccessSubmitRegisterForm() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .flashAttr("user", new RegisterUserDto("test", "test", "test")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void getLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login-form"));
    }

    @Test
    void getRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-form"))
                .andExpect(model().attribute("user",instanceOf(RegisterUserDto.class)));
    }


}
