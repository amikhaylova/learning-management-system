package com.mikhaylova.lms.controllerTest;

import com.mikhaylova.lms.controller.UserController;
import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.domain.Role;
import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.UserDto;
import com.mikhaylova.lms.service.AvatarStorageService;
import com.mikhaylova.lms.service.RoleService;
import com.mikhaylova.lms.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({UserController.class})
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private RoleService roleService;
    @MockBean
    private AvatarStorageService avatarStorageService;

    private User student;
    private User admin;
    private UserDto studentDto;
    private UserDto adminDto;

    @BeforeEach
    void init() {
        Course course1 = new Course(1L, "Вася", "Учим Java", new ArrayList<>(), new HashSet<>());
        Course course2 = new Course(2L, "Петя", "Учим С", new ArrayList<>(), new HashSet<>());

        Set<Course> userCourses = new HashSet<>();
        userCourses.add(course1);
        userCourses.add(course2);

        student = new User(1L, "student", userCourses, new HashSet<>(), "qwerty");
        admin = new User(2L, "admin", userCourses, new HashSet<>(), "qwerty");

        studentDto = new UserDto(1L, "student", Set.of(new Role("STUDENT")));
        adminDto = new UserDto(2L, "admin", new HashSet<>());

        initMocks();
    }

    @AfterEach
    void tearDown() {
        student = null;
        admin = null;
        studentDto = null;
        adminDto = null;
    }

    void initMocks() {
        Mockito.when(userService.findUserByUsername("student")).thenReturn(student);
        Mockito.when(userService.findUserByUsername("admin")).thenReturn(admin);
        Mockito.when(userService.findUserDtoById(1L)).thenReturn(studentDto);
        Mockito.when(userService.findUserDtoById(2L)).thenReturn(adminDto);
    }

    @Test
    void onlyAuthorizedUsersCanGetUserForm() throws Exception {
        mockMvc.perform(get("/user/{id}", 1L))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    void onlyAuthorizedUsersCanSubmitUserForm() throws Exception {
        mockMvc.perform(post("/user")
                .flashAttr("user", studentDto)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void notAdminCanNotGetUsersTable() throws Exception {
        mockMvc.perform(get("/user/admin/users"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void studentCanGetOnlyHisOwnUserForm() throws Exception {
        mockMvc.perform(get("/user/{id}", 2L))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanGetNotOnlyHisUserForm() throws Exception {
        mockMvc.perform(get("/user/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("user-form"))
                .andExpect(model().attribute("activePage", "users"))
                .andExpect(model().attribute("user", hasProperty("id", is(1L))));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminGetHisOwnUserForm() throws Exception {
        mockMvc.perform(get("/user/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(view().name("user-form"))
                .andExpect(model().attribute("activePage", "user"))
                .andExpect(model().attribute("user", hasProperty("id", is(2L))));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkUserFormValidation() throws Exception {
        mockMvc.perform(post("/user")
                .flashAttr("user", new UserDto(1L, "", null))
                .with(csrf()))
                .andExpect(view().name("user-form"))
                .andExpect(model().attributeHasFieldErrors("user", "username", "roles"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkSuccessUserFormSubmit() throws Exception {
        mockMvc.perform(post("/user")
                .flashAttr("user", studentDto)
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/course"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void successGetUsersTable() throws Exception {
        mockMvc.perform(get("/user/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users-table"))
                .andExpect(model().attribute("activePage", "users"));
    }


}
