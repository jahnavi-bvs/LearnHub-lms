package com.learnhub.assessment.assessment;

import com.learnhub.assessment.client.CourseClient;
import com.learnhub.assessment.client.CourseSummary;
import com.learnhub.assessment.client.EnrollmentClient;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Not @Transactional at class level on purpose: remote calls to course/enrollment-service
 * happen OUTSIDE database transactions. Each repository call runs its own short transaction,
 * and @EntityGraph loads everything we need in that one query.
 */
@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final CourseClient courseClient;
    private final EnrollmentClient enrollmentClient;

    public AssessmentService(AssessmentRepository assessmentRepository, CourseClient courseClient,
                             EnrollmentClient enrollmentClient) {
        this.assessmentRepository = assessmentRepository;
        this.courseClient = courseClient;
        this.enrollmentClient = enrollmentClient;
    }

    public AssessmentResponse create(CreateAssessmentRequest request, CurrentUser user) {
        CourseSummary course = courseClient.findCourse(request.courseId(), user.tokenValue())
                .orElseThrow(() -> new CourseNotFoundException(request.courseId()));
        if (!user.isAdmin() && !course.instructorId().equals(user.id())) {
            throw new AccessDeniedException("You can only create assessments for your own courses");
        }

        // Record the COURSE's instructor (not the caller - an admin may create it on their behalf)
        Assessment assessment = new Assessment(course.id(), course.instructorId(),
                request.title().trim(), request.description());
        request.questions().forEach(q ->
                assessment.addQuestion(new Question(q.text().trim(), q.options(), q.correctOptionIndex())));

        // cascade = ALL: one save() inserts the assessment, its questions and their options
        return AssessmentResponse.from(assessmentRepository.save(assessment), true);
    }

    public List<AssessmentSummary> listForCourse(Long courseId, CurrentUser user) {
        if (!user.isAdmin()) {
            if (user.isStudent()) {
                requireEnrolled(courseId, user);
            } else {
                // An instructor: must own the course (assessments may not exist yet, so ask course-service)
                CourseSummary course = courseClient.findCourse(courseId, user.tokenValue())
                        .orElseThrow(() -> new CourseNotFoundException(courseId));
                if (!course.instructorId().equals(user.id())) {
                    throw new AccessDeniedException("You can only view assessments for your own courses");
                }
            }
        }
        return assessmentRepository.findByCourseIdOrderByCreatedAtAsc(courseId).stream()
                .map(AssessmentSummary::from)
                .toList();
    }

    /** Owner/admin get the answer key; enrolled students get the questions only. */
    public AssessmentResponse get(Long id, CurrentUser user) {
        Assessment assessment = assessmentRepository.findWithQuestionsById(id)
                .orElseThrow(() -> new AssessmentNotFoundException(id));

        if (canManage(assessment, user)) {
            return AssessmentResponse.from(assessment, true);
        }
        if (user.isStudent()) {
            requireEnrolled(assessment.getCourseId(), user);
            return AssessmentResponse.from(assessment, false);
        }
        throw new AccessDeniedException("You can only view assessments for your own courses");
    }

    private boolean canManage(Assessment assessment, CurrentUser user) {
        // Local check - no network call, thanks to instructorId stored at creation
        return user.isAdmin() || assessment.getInstructorId().equals(user.id());
    }

    private void requireEnrolled(Long courseId, CurrentUser student) {
        if (!enrollmentClient.isEnrolled(courseId, student.tokenValue())) {
            throw new AccessDeniedException("You must be enrolled in the course to view its assessments");
        }
    }
}
