package com.mikhaylova.lms.repository;

import com.mikhaylova.lms.domain.AvatarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarImageRepository extends JpaRepository<AvatarImage, Long> {
    @Query("from AvatarImage ai left join ai.user u where u.id = :id")
    Optional<AvatarImage> findByUserId(Long id);
}
