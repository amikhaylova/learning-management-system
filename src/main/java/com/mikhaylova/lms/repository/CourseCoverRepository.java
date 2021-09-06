package com.mikhaylova.lms.repository;

import com.mikhaylova.lms.domain.CourseCover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseCoverRepository extends JpaRepository<CourseCover, Long> {
    @Query("from CourseCover cc left join cc.course c where c.id = :courseId")
    Optional<CourseCover> findByCourseId(Long courseId);
}
