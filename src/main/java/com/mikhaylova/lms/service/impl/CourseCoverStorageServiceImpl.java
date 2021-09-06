package com.mikhaylova.lms.service.impl;

import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.domain.CourseCover;
import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.repository.CourseCoverRepository;
import com.mikhaylova.lms.service.CourseCoverStorageService;
import com.mikhaylova.lms.service.CourseService;
import com.mikhaylova.lms.util.FileUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseCoverStorageServiceImpl implements CourseCoverStorageService {

    private final CourseCoverRepository courseCoverRepository;
    private final CourseService courseService;
    private final FileUtilService fileUtilService;

    @Autowired
    public CourseCoverStorageServiceImpl(CourseCoverRepository courseCoverRepository,
                                         CourseService courseService,
                                         FileUtilService fileUtilService) {
        this.courseCoverRepository = courseCoverRepository;
        this.courseService = courseService;
        this.fileUtilService = fileUtilService;
    }

    @Transactional
    public void save(Long courseId, String contentType, InputStream is) {
        Optional<CourseCover> opt = courseCoverRepository.findByCourseId(courseId);
        CourseCover courseCover;
        String filename;
        if (opt.isEmpty()) {
            filename = UUID.randomUUID().toString();
            Course course = courseService.getCourseById(courseId);
            courseCover = new CourseCover(null, contentType, filename, course);
        } else {
            courseCover = opt.get();
            filename = courseCover.getFilename();
            courseCover.setContentType(contentType);
        }
        courseCoverRepository.save(courseCover);
        fileUtilService.saveFile(is, filename);
    }

    public String getContentTypeByCourseId(Long courseId) {
        return courseCoverRepository.findByCourseId(courseId)
                .map(CourseCover::getContentType)
                .orElseThrow(() -> new NotFoundException(CourseCover.class.getSimpleName(), courseId));
    }

    public Optional<byte[]> getCourseCoverByCourseId(Long courseId) {
        return courseCoverRepository.findByCourseId(courseId)
                .map(CourseCover::getFilename)
                .map(fileUtilService::readFile);
    }
}
