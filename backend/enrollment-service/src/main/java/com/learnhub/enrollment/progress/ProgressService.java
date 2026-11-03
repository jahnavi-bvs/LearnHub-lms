package com.learnhub.enrollment.progress;

import com.learnhub.enrollment.client.CourseClient;
import com.learnhub.enrollment.enrollment.CurrentUser;
import com.learnhub.enrollment.enrollment.Enrollment;
import com.learnhub.enrollment.enrollment.EnrollmentNotFoundException;
import com.learnhub.enrollment.enrollment.EnrollmentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Like EnrollmentService: HTTP calls to course-service happen outside database transactions. */
@Service
public class ProgressService {

    private final EnrollmentRepository enrollmentRepository;
    private final ModuleCompletionRepository completionRepository;
    private final CourseClient courseClient;
    private final CompletionWriter completionWriter;

    public ProgressService(EnrollmentRepository enrollmentRepository, ModuleCompletionRepository completionRepository,
                           CourseClient courseClient, CompletionWriter completionWriter) {
        this.enrollmentRepository = enrollmentRepository;
        this.completionRepository = completionRepository;
        this.courseClient = courseClient;
        this.completionWriter = completionWriter;
    }

    public List<ProgressResponse> listMine(CurrentUser student) {
        return enrollmentRepository.findByStudentIdOrderByEnrolledAtDesc(student.id()).stream()
                .map(enrollment -> progressOf(enrollment, student))
                .toList();
    }

    /** Idempotent: marking a module that's already complete changes nothing. */
    public ProgressResponse markComplete(Long enrollmentId, Long moduleId, CurrentUser student) {
        Enrollment enrollment = ownEnrollment(enrollmentId, student);
        List<Long> moduleIds = courseClient.findModuleIds(enrollment.getCourseId(), student.tokenValue());
        if (!moduleIds.contains(moduleId)) {
            throw new ModuleNotInCourseException(moduleId);
        }
        if (!completionRepository.existsByEnrollmentIdAndModuleId(enrollmentId, moduleId)) {
            try {
                completionRepository.save(new ModuleCompletion(enrollment, moduleId));
            } catch (DataIntegrityViolationException e) {
                // A double-click: the other request already recorded it, which is the result we want
            }
        }
        return build(enrollment, moduleIds);
    }

    public ProgressResponse markIncomplete(Long enrollmentId, Long moduleId, CurrentUser student) {
        Enrollment enrollment = ownEnrollment(enrollmentId, student);
        completionWriter.remove(enrollmentId, moduleId);
        return progressOf(enrollment, student);
    }

    private ProgressResponse progressOf(Enrollment enrollment, CurrentUser student) {
        return build(enrollment, courseClient.findModuleIds(enrollment.getCourseId(), student.tokenValue()));
    }

    private ProgressResponse build(Enrollment enrollment, List<Long> moduleIds) {
        Set<Long> completed = completionRepository.findByEnrollmentId(enrollment.getId()).stream()
                .map(ModuleCompletion::getModuleId)
                .collect(Collectors.toSet());
        return ProgressResponse.of(enrollment.getId(), enrollment.getCourseId(), moduleIds, completed);
    }

    private Enrollment ownEnrollment(Long enrollmentId, CurrentUser student) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));
        if (!enrollment.getStudentId().equals(student.id())) {
            throw new AccessDeniedException("You can only update your own progress");
        }
        return enrollment;
    }

    /**
     * A separate bean because deleting needs a transaction, and @Transactional only takes effect
     * when a method is called through Spring's proxy, not from inside the same class.
     */
    @Service
    public static class CompletionWriter {

        private final ModuleCompletionRepository completionRepository;

        public CompletionWriter(ModuleCompletionRepository completionRepository) {
            this.completionRepository = completionRepository;
        }

        @Transactional
        public void remove(Long enrollmentId, Long moduleId) {
            completionRepository.deleteByEnrollmentIdAndModuleId(enrollmentId, moduleId);
        }
    }
}
