package com.learnhub.course.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // SELECT ... FROM courses WHERE published = true ORDER BY created_at DESC
    List<Course> findByPublishedTrueOrderByCreatedAtDesc();

    // SELECT ... FROM courses WHERE instructor_id = ? ORDER BY created_at DESC
    List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);
}
