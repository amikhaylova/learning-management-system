package com.mikhaylova.lms.serviceTest;

import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.dto.CourseDto;
import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.mapper.CourseMapper;
import com.mikhaylova.lms.repository.CourseRepository;
import com.mikhaylova.lms.service.CourseService;
import com.mikhaylova.lms.service.UserService;
import com.mikhaylova.lms.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserService userService;
    @Mock
    private CourseMapper courseMapper;
    private CourseService courseService;

    @BeforeEach
    void init(){
      courseService = new CourseServiceImpl(courseRepository, userService, courseMapper);
    }

    @Test
    void findCourseByIdShouldThrowException() {
        Mockito.when(courseRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> courseService.findCourseById(1L));
    }

    @Test
    void getCourseByIdShouldThrowException() {
        Mockito.when(courseRepository.getById(anyLong()))
                .thenReturn(null);
        Assertions.assertThrows(NotFoundException.class, () -> courseService.getCourseById(1L));
    }

    @Test
    void findCourseDtoByCourseIdThrowsException() {
        Mockito.when(courseRepository.findCourseDtoByCourseId(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> courseService.findCourseDtoByCourseId(1L));
    }

    @Test
    void checkSaveCourseAlreadyExists() {
        CourseService spyCourseService = spy(courseService);
        doReturn(new Course(1L, "", "", new ArrayList<>(), new HashSet<>()))
                .when(spyCourseService)
                .getCourseById(anyLong());
        spyCourseService.saveCourseDto(new CourseDto(1L, "", ""));
        verify(spyCourseService, times(1)).getCourseById(anyLong());
        verify(courseMapper, times(1)).mapCourseDtoToCourse(any(CourseDto.class), any(Course.class));
    }

    @Test
    void checkSaveNewCourseExists() {
        CourseService spyCourseService = spy(courseService);
        doReturn(new Course(1L, "", "", new ArrayList<>(), new HashSet<>())).when(spyCourseService).getCourseById(anyLong());
        spyCourseService.saveCourseDto(new CourseDto(null, "", ""));
        verify(spyCourseService, times(0)).getCourseById(anyLong());
        verify(courseMapper, times(1)).mapCourseDtoToCourse(any(CourseDto.class), any(Course.class));
    }

}
