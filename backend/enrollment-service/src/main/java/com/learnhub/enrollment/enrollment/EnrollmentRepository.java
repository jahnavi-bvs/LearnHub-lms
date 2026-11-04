package com.learnhub.enrollment.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentIdOrderByEnrolledAtDesc(Long studentId);

    List<Enrollment> findByCourseIdOrderByEnrolledAtAsc(Long courseId);

    @Query("""
            select new com.learnhub.enrollment.enrollment.CourseEnrollmentCount(e.courseId, count(e))
            from Enrollment e group by e.courseId order by count(e) desc
            """)
    List<CourseEnrollmentCount> countByCourse();
}
