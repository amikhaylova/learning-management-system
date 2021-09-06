package com.mikhaylova.lms.service;

import java.io.InputStream;
import java.util.Optional;

public interface CourseCoverStorageService {
    void save(Long courseId, String contentType, InputStream is);

    String getContentTypeByCourseId(Long courseId);

    Optional<byte[]> getCourseCoverByCourseId(Long courseId);
}
