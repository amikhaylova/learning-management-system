package com.mikhaylova.lms.serviceTest;

import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.repository.LessonRepository;
import com.mikhaylova.lms.service.LessonService;
import com.mikhaylova.lms.service.impl.LessonServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    @Mock
    private LessonRepository lessonRepository;
    private LessonService lessonService;

    @BeforeEach
    void init(){
        lessonService = new LessonServiceImpl(lessonRepository);
    }

    @Test
    void findLessonDtoByLessonIdThrowsException(){
        Mockito.when(lessonRepository.findLessonDtoByLessonId(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> lessonService.findLessonDtoByLessonId(1L));
    }

    @Test
    void findLessonByIdThrowsException(){
        Mockito.when(lessonRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> lessonService.findLessonById(1L));
    }
}
