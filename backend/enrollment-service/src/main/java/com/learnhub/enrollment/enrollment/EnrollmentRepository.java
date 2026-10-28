package com.learnhub.enrollment.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentIdOrderByEnrolledAtDesc(Long studentId);

    List<Enrollment> findByCourseIdOrderByEnrolledAtAsc(Long courseId);
}
