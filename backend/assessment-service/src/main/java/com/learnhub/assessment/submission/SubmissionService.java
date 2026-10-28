package com.learnhub.assessment.submission;

import com.learnhub.assessment.assessment.Assessment;
import com.learnhub.assessment.assessment.AssessmentService;
import com.learnhub.assessment.assessment.CurrentUser;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

/** Like AssessmentService: no class-level transaction, so remote calls never hold a DB connection. */
@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssessmentService assessmentService;

    public SubmissionService(SubmissionRepository submissionRepository, AssessmentService assessmentService) {
        this.submissionRepository = submissionRepository;
        this.assessmentService = assessmentService;
    }

    public SubmissionResultResponse submit(Long assessmentId, SubmitRequest request, CurrentUser student) {
        Assessment assessment = assessmentService.loadWithQuestions(assessmentId);
        assessmentService.requireEnrolled(assessment.getCourseId(), student);

        if (submissionRepository.existsByAssessmentIdAndStudentId(assessmentId, student.id())) {
            throw new AlreadySubmittedException(assessmentId);
        }

        String problem = Grader.validate(assessment.getQuestions(), request.answers());
        if (problem != null) {
            throw new InvalidSubmissionException(problem);
        }

        int score = Grader.score(assessment.getQuestions(), request.answers());
        Submission submission = new Submission(assessment, student.id(), student.name(), request.answers(),
                score, assessment.getQuestions().size());
        try {
            submission = submissionRepository.save(submission);
        } catch (DataIntegrityViolationException e) {
            // Double-click: two submissions at the same moment, the unique constraint rejects one
            throw new AlreadySubmittedException(assessmentId);
        }
        return SubmissionResultResponse.from(submission, assessment.getQuestions());
    }

    /** The student's own detailed result, including the correct answers. */
    public SubmissionResultResponse myResult(Long assessmentId, CurrentUser student) {
        Submission submission = submissionRepository.findByAssessmentIdAndStudentId(assessmentId, student.id())
                .orElseThrow(() -> new SubmissionNotFoundException(assessmentId));
        Assessment assessment = assessmentService.loadWithQuestions(assessmentId);
        return SubmissionResultResponse.from(submission, assessment.getQuestions());
    }

    /** All the student's results, across every course. */
    public List<SubmissionSummary> myResults(CurrentUser student) {
        return submissionRepository.findByStudentIdOrderBySubmittedAtDesc(student.id()).stream()
                .map(SubmissionSummary::from)
                .toList();
    }

    /** Everyone's scores for one assessment: owner or admin only. */
    public List<SubmissionSummary> listForAssessment(Long assessmentId, CurrentUser user) {
        Assessment assessment = assessmentService.loadWithQuestions(assessmentId);
        if (!assessmentService.canManage(assessment, user)) {
            throw new AccessDeniedException("You can only view submissions for your own assessments");
        }
        return submissionRepository.findByAssessmentIdOrderBySubmittedAtAsc(assessmentId).stream()
                .map(SubmissionSummary::from)
                .toList();
    }
}
