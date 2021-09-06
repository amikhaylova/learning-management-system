package com.mikhaylova.lms.repository;

import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("from User u " +
            "where u.id not in ( " +
            "select u.id " +
            "from User u " +
            "left join u.courses c " +
            "where c.id = :courseId)")
    List<User> findUsersNotAssignedToCourse(@Param("courseId") long courseId);

    Optional<User> findUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("select new com.mikhaylova.lms.dto.UserDto(u.id, u.username) " +
            "from User u order by u.id")
    List<UserDto> findAllUserDto();
}
