package com.mikhaylova.lms.controller;

import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.exception.InternalServerError;
import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.exception.UserNotAssignedToCourseException;
import com.mikhaylova.lms.mapper.CourseMapper;
import com.mikhaylova.lms.mapper.UserMapper;
import com.mikhaylova.lms.service.CourseCoverStorageService;
import com.mikhaylova.lms.service.CourseService;
import com.mikhaylova.lms.service.LessonService;
import com.mikhaylova.lms.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;

@Controller
@RequestMapping("/course")
public class CourseController {
    private CourseService courseService;
    private UserService userService;
    private LessonService lessonService;
    private UserMapper userMapper;
    private CourseMapper courseMapper;
    private CourseCoverStorageService courseCoverStorageService;

    public CourseController(CourseService courseService,
                            UserService userService,
                            LessonService lessonService,
                            UserMapper userMapper,
                            CourseMapper courseMapper,
                            CourseCoverStorageService courseCoverStorageService) {
        this.courseService = courseService;
        this.userService = userService;
        this.lessonService = lessonService;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
        this.courseCoverStorageService = courseCoverStorageService;
    }

    @ModelAttribute("principalId")
    public Long principalIdAttribute(Principal principal) {
        if (principal != null)
            return userService.findUserByUsername(principal.getName()).getId();
        return null;
    }


    @GetMapping
    public String courseTable(Model model,
                              @RequestParam(name = "titlePrefix", required = false) String titlePrefix,
                              @Min(0) @RequestParam(name = "page", required = false) Integer page) {
        Page<Course> courses = courseService.findCourses(titlePrefix, page);
        int currentPage;
        if (page == null)
            currentPage = 0;
        else
            currentPage = page;
        model.addAttribute("courses", courseMapper.mapCourseListToCourseDtoList(courses.getContent()));
        model.addAttribute("activePage", "courses");
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("titlePrefix", titlePrefix);
        model.addAttribute("pages", (int) Math.ceil((double) courses.getTotalElements() / 3));
        return "course-table";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String courseForm(Model model,
                             @PathVariable("id") Long id,
                             HttpServletRequest request) {
        if (!request.isUserInRole("ROLE_ADMIN")) {
            User user = userService.findUserByUsername(request.getUserPrincipal().getName());
            if (!user.getCourses().contains(courseService.getCourseById(id)))
                throw new UserNotAssignedToCourseException("User " + id + " not assigned to course.");
        }
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", courseService.findCourseDtoByCourseId(id));
        model.addAttribute("lessons", lessonService.findAllForLessonIdWithoutText(id));
        model.addAttribute("users", userMapper.mapUserListToUserDtoList(new ArrayList<>(course.getUsers())));
        return "course-form";
    }

    /* @PreAuthorize("isAuthenticated()")*/
    @GetMapping("/{id}/cover")
    public ResponseEntity<byte[]> courseCover(@PathVariable("id") Long id) {
        String contentType = courseCoverStorageService.getContentTypeByCourseId(id);
        byte[] data = courseCoverStorageService.getCourseCoverByCourseId(id)
                .orElseThrow(NotFoundException::new);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/cover")
    public String updateCourseCover(@RequestParam("cover") MultipartFile avatar,
                                    @PathVariable("id") Long id) {
        try {
            courseCoverStorageService.save(id, avatar.getContentType(), avatar.getInputStream());
        } catch (Exception ex) {
            throw new InternalServerError();
        }
        return "redirect:/course/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{courseId}/assign")
    public String assignUserForm(@PathVariable("courseId") Long courseId,
                                 @RequestParam("userId") Long userId,
                                 HttpServletRequest request) {
        String principalName = request.getUserPrincipal().getName();
        Long principalId = userService.findUserByUsername(principalName).getId();
        //предотвращаем попытку авторизованного пользователя (если он не адмиистратор)
        // назначить на курс кого-то, кроме себя
        if (!request.isUserInRole("ROLE_ADMIN") && !userId.equals(principalId))
            throw new AccessDeniedException("Access denied");
        courseService.assignUser(courseId, userId);
        return "redirect:/course";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{courseId}/assign")
    public String assignUserForm(@PathVariable("courseId") Long courseId, Model model, HttpServletRequest request) {
        if (courseService.existsById(courseId)) {
            model.addAttribute("courseId", courseId);
            if (request.isUserInRole("ROLE_ADMIN")) {
                model.addAttribute("users", userService.findUsersNotAssignedToCourse(courseId));
            } else {
                User user = userService.findUserByUsername(request.getRemoteUser());
                model.addAttribute("users", Collections.singletonList(user));
            }
            return "assign-user";
        } else {
            throw new NotFoundException(Course.class.getSimpleName(), courseId);
        }

    }

}