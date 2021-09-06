package com.mikhaylova.lms.controllerTest;

import com.mikhaylova.lms.controller.CourseController;
import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.CourseDto;
import com.mikhaylova.lms.dto.LessonDto;
import com.mikhaylova.lms.dto.UserDto;
import com.mikhaylova.lms.mapper.CourseMapper;
import com.mikhaylova.lms.mapper.UserMapper;
import com.mikhaylova.lms.service.CourseCoverStorageService;
import com.mikhaylova.lms.service.CourseService;
import com.mikhaylova.lms.service.LessonService;
import com.mikhaylova.lms.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CourseController.class})
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CourseService courseService;
    @MockBean
    private UserService userService;
    @MockBean
    private LessonService lessonService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private CourseMapper courseMapper;
    @MockBean
    private CourseCoverStorageService courseCoverStorageService;

    private List<Course> coursesList;
    private List<CourseDto> courseDtoList;
    private User student;
    private User admin;
    private List<LessonDto> lessonDtoList;
    private List<UserDto> userDtoList;
    private List<User> usersList;
    private CourseDto course1Dto;

    void initMocks() {
        Mockito.when(userService.findUserByUsername("admin")).thenReturn(admin);
        Mockito.when(userService.findUserByUsername("student")).thenReturn(student);
        Mockito.when(courseService.findCourses(null, null)).thenReturn(new PageImpl<>(coursesList));
        Mockito.when(courseMapper.mapCourseListToCourseDtoList(Mockito.anyList())).thenReturn(courseDtoList);
        Mockito.when(courseService.getCourseById(3L)).thenReturn(coursesList.get(2));
        Mockito.when(courseService.getCourseById(1L)).thenReturn(coursesList.get(0));
        Mockito.when(courseService.findCourseDtoByCourseId(anyLong())).thenReturn(course1Dto);
        Mockito.when(lessonService.findAllForLessonIdWithoutText(anyLong())).thenReturn(lessonDtoList);
        Mockito.when(userMapper.mapUserListToUserDtoList(anyList())).thenReturn(userDtoList);
        Mockito.when(courseService.getCourseById(3L)).thenReturn(coursesList.get(2));
        Mockito.when(userService.findUsersNotAssignedToCourse(anyLong())).thenReturn(usersList);
    }

    @BeforeEach
    void init() {
        userDtoList = new ArrayList<>();
        userDtoList.add(new UserDto(1L, "student"));
        userDtoList.add(new UserDto(2L, "student1"));
        userDtoList.add(new UserDto(3L, "admin"));

        usersList = new ArrayList<>();
        usersList.add(new User("student", "qwerty", new HashSet<>()));
        usersList.add(new User("student1", "qwerty", new HashSet<>()));
        usersList.add(new User("student2", "qwerty", new HashSet<>()));

        Course course1 = new Course(1L, "Вася", "Учим Java", new ArrayList<>(), new HashSet<>());
        Course course2 = new Course(2L, "Петя", "Учим С", new ArrayList<>(), new HashSet<>());
        Course course3 = new Course(3L, "Антон", "Учим Python", new ArrayList<>(), new HashSet<>());

        course1Dto = new CourseDto(1L, "Вася", "Учим Java");

        coursesList = new ArrayList<>();
        coursesList.add(course1);
        coursesList.add(course2);
        coursesList.add(course3);

        courseDtoList = new ArrayList<>();
        courseDtoList.add(new CourseDto(1L, "Вася", "Учим Java"));
        courseDtoList.add(new CourseDto(2L, "Петя", "Учим С"));
        courseDtoList.add(new CourseDto(3L, "Антон", "Учим Python"));

        Set<Course> userCourses = new HashSet<>();
        userCourses.add(course1);
        userCourses.add(course2);

        student = new User(1L, "student", userCourses, new HashSet<>(), "qwerty");
        admin = new User(2L, "admin", userCourses, new HashSet<>(), "qwerty");

        lessonDtoList = new ArrayList<>();
        lessonDtoList.add(new LessonDto(1L, "Урок №1", 1L));
        lessonDtoList.add(new LessonDto(2L, "Урок №2", 1L));

        initMocks();
    }

    @AfterEach
    void tearDown() {
        coursesList = null;
        courseDtoList = null;
        student = null;
        admin = null;
        lessonDtoList = null;
        userDtoList = null;
        usersList = null;
        course1Dto = null;
    }

    @Test
    void courseTableEntryShouldAddCoursesToModel() throws Exception {
        mockMvc.perform(get("/course"))
                .andExpect(status().isOk())
                .andExpect(view().name("course-table"))
                .andExpect(model().attribute("courses", courseDtoList))
                .andExpect(model().attribute("activePage", "courses"));
        verify(courseService, times(1)).findCourses(null, null);
        verify(courseMapper, times(1)).mapCourseListToCourseDtoList(coursesList);
        verifyNoMoreInteractions(courseService);
        verifyNoMoreInteractions(courseMapper);

    }

    @Test
    void nonAuthorizedUserCanNotGetCourseForm() throws Exception {
        mockMvc.perform(get("/course/{id}", 1L))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void studentCantGetInformationAboutCourseHeIsNotAssignedTo() throws Exception {
        mockMvc.perform(get("/course/{id}", 3L))
                .andExpect(status().isForbidden())
                .andExpect(view().name("not-assigned"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void studentCanGetInformationAboutCourseHeIsAssignedTo() throws Exception {
        mockMvc.perform(get("/course/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("course-form"))
                .andExpect(model().attribute("course", course1Dto))
                .andExpect(model().attribute("lessons", lessonDtoList))
                .andExpect(model().attribute("users", userDtoList))
                .andExpect(model().attribute("principalId", student.getId()));
        verify(courseService, times(2)).getCourseById(anyLong());
        verify(courseService, times(1)).findCourseDtoByCourseId(anyLong());
        verify(userService, times(2)).findUserByUsername("student");
        verify(lessonService, times(1)).findAllForLessonIdWithoutText(coursesList.get(0).getId());
        verify(userMapper, times(1)).mapUserListToUserDtoList(anyList());
        verifyNoMoreInteractions(courseService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(lessonService);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanGetInformationAboutCourseHeIsNotAssignedTo() throws Exception {
        mockMvc.perform(get("/course/{id}", 3L))
                .andExpect(status().isOk())
                .andExpect(view().name("course-form"))
                .andExpect(model().attribute("course", course1Dto))
                .andExpect(model().attribute("lessons", lessonDtoList))
                .andExpect(model().attribute("users", userDtoList))
                .andExpect(model().attribute("principalId", admin.getId()));
        verify(courseService, times(1)).getCourseById(anyLong());
        verify(courseService, times(1)).findCourseDtoByCourseId(anyLong());
        verify(userService, times(1)).findUserByUsername("admin");
        verify(lessonService, times(1)).findAllForLessonIdWithoutText(coursesList.get(2).getId());
        verify(userMapper, times(1)).mapUserListToUserDtoList(anyList());
        verifyNoMoreInteractions(courseService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(lessonService);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void onlyAdminGetSubmitCourseForm() throws Exception {
        mockMvc.perform(post("/course")
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void onlyAdminCanCreateNewCourse() throws Exception {
        mockMvc.perform(get("/course/new")
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkFieldValidationCourseForm() throws Exception {
        mockMvc.perform(post("/course")
                .with(csrf())
                .flashAttr("course", new CourseDto(1L, "", "")))
                .andExpect(view().name("course-form"))
                .andExpect(model().attributeHasFieldErrors("course", "title", "author"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkSuccessSubmitOfCourseForm() throws Exception {
        mockMvc.perform(post("/course")
                .with(csrf())
                .flashAttr("course", new CourseDto(1L, "Конфуций", "Жизнь есть страдание")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/course"));
    }


    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void studentCanNotDeleteCourse() throws Exception {
        mockMvc.perform(delete("/course/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanDeleteCourse() throws Exception {
        mockMvc.perform(delete("/course/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/course"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void studentCanNotAssignOtherUserToCourse() throws Exception {
        mockMvc.perform(post("/course/{courseId}/assign", 1L)
                .with(csrf())
                .param("userId", String.valueOf(student.getId() + 1)))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void studentCanAssignHimselfToCourse() throws Exception {
        mockMvc.perform(post("/course/{courseId}/assign", 1L)
                .with(csrf())
                .param("userId", String.valueOf(student.getId())))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/course"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanAssignUserToCourse() throws Exception {
        mockMvc.perform(post("/course/{courseId}/assign", 1L)
                .with(csrf())
                .param("userId", String.valueOf(student.getId())))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/course"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkNaNRequestParamAssignUserToCourse() throws Exception {
        mockMvc.perform(post("/course/{courseId}/assign", 1L)
                .with(csrf())
                .param("userId", "blablabla"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkEmptyRequestParamAssignUserToCourse() throws Exception {
        mockMvc.perform(post("/course/{courseId}/assign", 1L)
                .with(csrf())
                .param("userId", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void studentCanNotUnassignUserFromCourse() throws Exception {
        mockMvc.perform(delete("/course/{courseId}/assign", 1L)
                .with(csrf())
                .param("userId", String.valueOf(student.getId() + 1)))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanUnassignUserFromCourse() throws Exception {
        mockMvc.perform(delete("/course/{courseId}/assign", 1L)
                .with(csrf())
                .param("userId", String.valueOf(student.getId())))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/course/" + 1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkNaNUserIdParamUnassignUserFromCourse() throws Exception {
        mockMvc.perform(delete("/course/{courseId}/assign", 1L)
                .with(csrf())
                .param("userId", "blablabla"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkEmptyUserIdParamUnassignUserFromCourse() throws Exception {
        mockMvc.perform(delete("/course/{courseId}/assign", 1L)
                .with(csrf())
                .param("userId", ""))
                .andExpect(status().isBadRequest());
    }


    @Test
    void notAuthorizedUserDoesNotHaveAccessToAssignForm() throws Exception {
        mockMvc.perform(get("/course/{courseId}/assign", 1L))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkAssignToCourseFormWhichDoesNotExist() throws Exception {
        Mockito.when(courseService.existsById(anyLong())).thenReturn(false);
        mockMvc.perform(get("/course/{courseId}/assign", 1L))
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void checkAdminAccessAssignToCourseForm() throws Exception {
        Mockito.when(courseService.existsById(anyLong())).thenReturn(true);
        mockMvc.perform(get("/course/{courseId}/assign", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("assign-user"))
                .andExpect(model().attribute("users", usersList));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void checkGetSingletonListInAssignToCourseForm() throws Exception {
        Mockito.when(courseService.existsById(anyLong())).thenReturn(true);
        mockMvc.perform(get("/course/{courseId}/assign", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("assign-user"))
                .andExpect(model().attribute("users", hasSize(1)));
    }

}
