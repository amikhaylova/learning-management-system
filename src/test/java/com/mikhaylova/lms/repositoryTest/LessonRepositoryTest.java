package com.mikhaylova.lms.repositoryTest;

import com.mikhaylova.lms.dto.LessonDto;
import com.mikhaylova.lms.repository.LessonRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
class LessonRepositoryTest {

    @Autowired
    private LessonRepository lessonRepository;

    @Test
    void shouldReturnAllLessonsWithoutTextByCourseId() {
        List<LessonDto> actual = lessonRepository.findAllForLessonIdWithoutText(1L);
        Assertions.assertEquals(3, actual.size());
        actual.stream()
                .map(LessonDto::getText)
                .forEach(Assertions::assertNull);
    }

    @Test
    void listShouldBeEmptyWhenCourseDoesNotExist() {
        List<LessonDto> actual = lessonRepository.findAllForLessonIdWithoutText(16L);
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    void shouldReturnLessonDto() {
        Optional<LessonDto> actual = lessonRepository.findLessonDtoByLessonId(1L);
        LessonDto expected = new LessonDto(1L, "American Samurai (Ninja: American Samurai)",
                "Morbi non lectus. Aliquam sit amet diam in magna bibendum imperdiet. " +
                        "Nullam orci pede, venenatis non, sodales sed, tincidunt eu, felis.", 12L);
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    void shouldNotPresentIfLessonIdDoesNotExist() {
        Optional<LessonDto> actual = lessonRepository.findLessonDtoByLessonId(46L);
        Assertions.assertFalse(actual.isPresent());
    }

}
