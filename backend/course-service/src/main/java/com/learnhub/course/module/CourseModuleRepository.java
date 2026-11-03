package com.learnhub.course.module;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {

    // "CourseId" walks the relationship: WHERE course_id = ?
    List<CourseModule> findByCourseIdOrderByPositionAsc(Long courseId);

    // Requiring both ids means a module can't be reached through a different course's URL
    Optional<CourseModule> findByIdAndCourseId(Long id, Long courseId);

    boolean existsByCourseIdAndTitle(Long courseId, String title);

    boolean existsByCourseIdAndTitleAndIdNot(Long courseId, String title, Long id);

    int countByCourseId(Long courseId);
}
