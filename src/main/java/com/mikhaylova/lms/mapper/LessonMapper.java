package com.mikhaylova.lms.mapper;

import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.domain.Lesson;
import com.mikhaylova.lms.dto.LessonDto;
import com.mikhaylova.lms.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LessonMapper {

    private CourseRepository courseRepository;

    @Autowired
    public LessonMapper(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Lesson mapLessonDtoToLesson(LessonDto lessonDto) {
        Course course = courseRepository.getById(lessonDto.getCourseId());
        return new Lesson(
                lessonDto.getId(),
                lessonDto.getTitle(),
                lessonDto.getText(),
                course
        );

    }
}
