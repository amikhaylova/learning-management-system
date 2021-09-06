package com.mikhaylova.lms.controller;

import com.mikhaylova.lms.domain.Lesson;
import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.LessonDto;
import com.mikhaylova.lms.exception.UserNotAssignedToCourseException;
import com.mikhaylova.lms.mapper.LessonMapper;
import com.mikhaylova.lms.service.LessonService;
import com.mikhaylova.lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/lesson")
public class LessonController {

    private LessonService lessonService;
    private LessonMapper lessonMapper;
    private UserService userService;

    @Autowired
    public LessonController(LessonService lessonService,
                            LessonMapper lessonMapper,
                            UserService userService) {
        this.lessonService = lessonService;
        this.lessonMapper = lessonMapper;
        this.userService = userService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String newLessonForm(Model model, @RequestParam("course_id") Long courseId) {
        model.addAttribute("lessonDto", new LessonDto(courseId));
        return "lesson-form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String editLessonForm(Model model,
                                 @PathVariable("id") Long id,
                                 HttpServletRequest request) {
        if (!request.isUserInRole("ROLE_ADMIN")) {
            Lesson lesson = lessonService.findLessonById(id);
            User user = userService.findUserByUsername(request.getUserPrincipal().getName());
            if (!user.getCourses().contains(lesson.getCourse()))
                throw new UserNotAssignedToCourseException("User " + id + " not assigned to course.");
        }
        model.addAttribute("lessonDto", lessonService.findLessonDtoByLessonId(id));
        return "lesson-form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public String submitLessonForm(@Valid LessonDto lessonDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "lesson-form";
        }
        Lesson mappedLesson = lessonMapper.mapLessonDtoToLesson(lessonDto);
        lessonService.saveLesson(mappedLesson);
        return "redirect:/course/" + lessonDto.getCourseId();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public String deleteLesson(@PathVariable("id") Long id, @RequestParam("course_id") Long courseId) {
        lessonService.deleteLesson(id);
        return "redirect:/course/" + courseId;
    }

}
