package com.learnhub.enrollment.enrollment;

import java.time.Instant;

public record EnrollmentResponse(Long id, Long studentId, String studentName, Long courseId, String courseTitle,
                                 Instant enrolledAt) {

    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(enrollment.getId(), enrollment.getStudentId(), enrollment.getStudentName(),
                enrollment.getCourseId(), enrollment.getCourseTitle(), enrollment.getEnrolledAt());
    }
}
