package com.mikhaylova.lms.controller.adminController;

import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.dto.CourseDto;
import com.mikhaylova.lms.mapper.UserMapper;
import com.mikhaylova.lms.service.CourseService;
import com.mikhaylova.lms.service.LessonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/course")
public class CourseAdminController {
    private final CourseService courseService;
    private final LessonService lessonService;
    private final UserMapper userMapper;

    public CourseAdminController(CourseService courseService,
                                 LessonService lessonService,
                                 UserMapper userMapper) {
        this.courseService = courseService;
        this.lessonService = lessonService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public String submitCourseForm(@Valid @ModelAttribute("course") CourseDto course,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            if (course.getId() != null) {
                Course c = courseService.getCourseById(course.getId());
                model.addAttribute("lessons", lessonService.findAllForLessonIdWithoutText(course.getId()));
                model.addAttribute("users", userMapper.mapUserListToUserDtoList(new ArrayList<>(c.getUsers())));
            } else {
                //если id курса == null, это значит, что форма используется для создания курса, а не редактирования
                //т.е. смысла отображать таблицы уроков и пользователей нет
                model.addAttribute("lessons", null);
                model.addAttribute("users", null);
            }
            return "course-form";
        }
        courseService.saveCourseDto(course);
        return "redirect:/course";
    }

    @GetMapping("/new")
    public String courseForm(Model model) {
        model.addAttribute("course", new CourseDto());
        return "course-form";
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        System.out.println(id);
        courseService.deleteCourse(id);
        return "redirect:/course";
    }

    @DeleteMapping("/{courseId}/assign")
    public String unassignUserForm(@PathVariable("courseId") Long courseId,
                                   @RequestParam("userId") Long userId) {
        courseService.unassignUser(courseId, userId);
        return "redirect:/course/" + courseId;
    }

}
