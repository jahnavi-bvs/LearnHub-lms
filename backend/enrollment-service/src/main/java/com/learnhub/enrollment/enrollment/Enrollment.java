package com.learnhub.enrollment.enrollment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * A student's place in a course. studentId and courseId refer to rows in OTHER
 * services' databases, so they are plain numbers - not foreign keys.
 */
@Entity
@Table(name = "enrollments",
        // The database itself guarantees a student can't be enrolled in the same course twice
        uniqueConstraints = @UniqueConstraint(name = "uk_enrollment_student_course",
                columnNames = {"student_id", "course_id"}))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(nullable = false, length = 100)
    private String studentName;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    // Copied at enrollment time so "my enrollments" needs no call to course-service
    @Column(nullable = false, length = 200)
    private String courseTitle;

    @Column(nullable = false, updatable = false)
    private Instant enrolledAt;

    protected Enrollment() {
    }

    public Enrollment(Long studentId, String studentName, Long courseId, String courseTitle) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
    }

    @PrePersist
    void onCreate() {
        enrolledAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }
}
