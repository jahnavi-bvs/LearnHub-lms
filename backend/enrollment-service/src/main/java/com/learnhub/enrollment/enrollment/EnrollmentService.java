package com.learnhub.enrollment.enrollment;

import com.learnhub.enrollment.client.CourseClient;
import com.learnhub.enrollment.client.CourseSummary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseClient courseClient) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseClient = courseClient;
    }

    /**
     * Deliberately NOT @Transactional: the HTTP call to course-service happens first,
     * with no database connection held open while we wait for it. save() then runs
     * in its own short transaction.
     */
    public EnrollmentResponse enroll(EnrollRequest request, CurrentUser student) {
        Long courseId = request.courseId();

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.id(), courseId)) {
            throw new AlreadyEnrolledException(courseId);
        }

        // No token relayed: course-service shows anonymous callers only PUBLISHED courses,
        // so drafts come back as 404 and can't be enrolled in.
        CourseSummary course = courseClient.findCourse(courseId, null)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        try {
            Enrollment saved = enrollmentRepository.save(
                    new Enrollment(student.id(), student.name(), course.id(), course.title()));
            return EnrollmentResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            // Two identical requests at the same moment: the unique constraint rejects the second
            throw new AlreadyEnrolledException(courseId);
        }
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> listMine(CurrentUser student) {
        return enrollmentRepository.findByStudentIdOrderByEnrolledAtDesc(student.id()).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    /** Roster: only the course's own instructor (or an admin) may see who is enrolled. */
    public List<EnrollmentResponse> listForCourse(Long courseId, CurrentUser user) {
        // Relay the caller's token so course-service applies its own visibility rules
        CourseSummary course = courseClient.findCourse(courseId, user.tokenValue())
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        if (!user.isAdmin() && !course.instructorId().equals(user.id())) {
            throw new AccessDeniedException("You can only view enrollments for your own courses");
        }
        return enrollmentRepository.findByCourseIdOrderByEnrolledAtAsc(courseId).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    @Transactional
    public void cancel(Long enrollmentId, CurrentUser user) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));

        if (!user.isAdmin() && !enrollment.getStudentId().equals(user.id())) {
            throw new AccessDeniedException("You can only cancel your own enrollments");
        }
        enrollmentRepository.delete(enrollment);
    }
}
