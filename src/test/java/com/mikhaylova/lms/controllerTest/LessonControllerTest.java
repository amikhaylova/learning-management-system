package com.mikhaylova.lms.controllerTest;

import com.mikhaylova.lms.controller.LessonController;
import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.domain.Lesson;
import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.LessonDto;
import com.mikhaylova.lms.mapper.LessonMapper;
import com.mikhaylova.lms.service.LessonService;
import com.mikhaylova.lms.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({LessonController.class})
class LessonControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LessonService lessonService;
    @MockBean
    private LessonMapper lessonMapper;
    @MockBean
    private UserService userService;

    private Lesson lesson;
    private User student;
    private User admin;
    private LessonDto lessonDto;

    @BeforeEach
    void init() {
        Course course1 = new Course(1L, "Вася", "Учим Java", new ArrayList<>(), new HashSet<>());
        Course course2 = new Course(2L, "Петя", "Учим С", new ArrayList<>(), new HashSet<>());
        Course course3 = new Course(3L, "Антон", "Учим Python", new ArrayList<>(), new HashSet<>());

        lesson = new Lesson(1L, "Best lesson ever", "blabla", course3);
        lessonDto = new LessonDto(1L, "Best lesson ever", "blabla", 3L);

        Set<Course> userCourses = new HashSet<>();
        userCourses.add(course1);
        userCourses.add(course2);

        student = new User(1L, "student", userCourses, new HashSet<>(), "qwerty");
        admin = new User(2L, "admin", userCourses, new HashSet<>(), "qwerty");

        initMocks();
    }

    void initMocks() {
        Mockito.when(lessonService.findLessonById(1L)).thenReturn(lesson);
        Mockito.when(userService.findUserByUsername("student")).thenReturn(student);
        Mockito.when(userService.findUserByUsername("admin")).thenReturn(admin);
        Mockito.when(lessonService.findLessonDtoByLessonId(anyLong())).thenReturn(lessonDto);
        Mockito.when(lessonMapper.mapLessonDtoToLesson(ArgumentMatchers.any(LessonDto.class))).thenReturn(lesson);
    }

    @AfterEach
    void tearDown() {
        lesson = null;
        student = null;
        admin = null;
        lessonDto = null;
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void notAdminHasNotAccessToNewLessonForm() throws Exception {
        mockMvc.perform(get("/lesson/new")
                .param("course_id", String.valueOf(1L)))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void notAdminHasNotAccessToSubmitLessonForm() throws Exception {
        mockMvc.perform(post("/lesson")
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void notAdminHasNotAccessToDeleteLesson() throws Exception {
        mockMvc.perform(delete("/lesson/{id}", 1L)
                .with(csrf())
                .param("course_id", String.valueOf(1L)))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    void notAuthorizedUsersHasNotAccessToEditLessonForm() throws Exception {
        mockMvc.perform(get("/lesson/{id}", 1L))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void newLessonFormValidModelAttributeIsAdded() throws Exception {
        mockMvc.perform(get("/lesson/new")
                .param("course_id", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(view().name("lesson-form"))
                .andExpect(model().attribute("lessonDto", hasProperty("courseId", is(1L))));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void studentCanNotGetLessonOfCourseHeIsNotAssignedTo() throws Exception {
        mockMvc.perform(get("/lesson/{id}", 1L))
                .andExpect(status().isForbidden())
                .andExpect(view().name("not-assigned"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanGetLessonOfCourseHeIsNotAssignedTo() throws Exception {
        mockMvc.perform(get("/lesson/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(model().attribute("lessonDto", lessonDto))
                .andExpect(view().name("lesson-form"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkFieldValidationLessonForm() throws Exception {
        mockMvc.perform(post("/lesson")
                .with(csrf())
                .flashAttr("lessonDto", new LessonDto(1L, "", "", 1L)))
                .andExpect(view().name("lesson-form"))
                .andExpect(model().attributeHasFieldErrors("lessonDto", "title", "text"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkSuccessSubmitOfLessonForm() throws Exception {
        mockMvc.perform(post("/lesson")
                .with(csrf())
                .flashAttr("lessonDto", lessonDto))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/course/" + lessonDto.getCourseId()));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void studentCanNotDeleteLesson() throws Exception {
        mockMvc.perform(delete("/lesson/{id}", 1L)
                .param("course_id", String.valueOf(1L))
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanDeleteLesson() throws Exception {
        mockMvc.perform(delete("/lesson/{id}", 1L)
                .param("course_id", String.valueOf(1L))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/course/" + 1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void DeleteLessonInvalidCourseIdParam() throws Exception {
        mockMvc.perform(delete("/lesson/{id}", 1L)
                .param("course_id", "blabla")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void DeleteLessonEmptyCourseIdParam() throws Exception {
        mockMvc.perform(delete("/lesson/{id}", 1L)
                .param("course_id", "")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }


}
