package com.mikhaylova.lms.repositoryTest;

import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.dto.CourseDto;
import com.mikhaylova.lms.repository.CourseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    CourseRepository courseRepository;
    @Test
    void shouldReturnCoursesByTitleLike() {
        Page<Course> courses = courseRepository.findByTitleLikeIgnoreCaseOrderById("b%", null);
        Set<String> actual = courses.stream()
                .map(Course::getTitle)
                .collect(Collectors.toSet());
        Set<String> expected = Set.of("Bigger Than the Sky", "Best of Times, The");
        Assertions.assertEquals(2, courses.getContent().size());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void shouldReturnFullListWithEmptyTitlePrefix() {
        Page<Course> courses = courseRepository.findByTitleLikeIgnoreCaseOrderById("%", null);
        Assertions.assertEquals(15, courses.getContent().size());
    }

    @Test
    void shouldReturnCourseDtoById() {
        Optional<CourseDto> actual = courseRepository.findCourseDtoByCourseId(1L);
        CourseDto expected = new CourseDto(1L, "Alejandra Durber", "Lolita");
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    void shouldNotPresentWhenReturnCourseDtoByNotExistingId() {
        Optional<CourseDto> actual = courseRepository.findCourseDtoByCourseId(16L);
        Assertions.assertFalse(actual.isPresent());
    }

}
