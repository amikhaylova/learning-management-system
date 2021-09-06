package com.mikhaylova.lms.mapper;

import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.dto.CourseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseMapper {
    public List<CourseDto> mapCourseListToCourseDtoList(List<Course> courses) {
        return courses.stream()
                .map(c -> new CourseDto(c.getId(), c.getAuthor(), c.getTitle()))
                .collect(Collectors.toList());
    }

    public void mapCourseDtoToCourse(CourseDto courseDto, Course course) {
        course.setTitle(courseDto.getTitle());
        course.setAuthor(courseDto.getAuthor());
    }
}
