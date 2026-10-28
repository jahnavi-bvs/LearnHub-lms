package com.learnhub.assessment.submission;

import com.learnhub.assessment.assessment.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Submissions are nested under their assessment: /api/assessments/{id}/submissions */
@RestController
@RequestMapping("/api/assessments")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/{id}/submissions")
    public ResponseEntity<SubmissionResultResponse> submit(@PathVariable Long id,
                                                           @Valid @RequestBody SubmitRequest request,
                                                           @AuthenticationPrincipal Jwt jwt) {
        SubmissionResultResponse result = submissionService.submit(id, request, CurrentUser.from(jwt));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/mine").build().toUri();
        return ResponseEntity.created(location).body(result);
    }

    @GetMapping("/{id}/submissions/mine")
    public SubmissionResultResponse myResult(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return submissionService.myResult(id, CurrentUser.from(jwt));
    }

    @GetMapping("/{id}/submissions")
    public List<SubmissionSummary> listForAssessment(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return submissionService.listForAssessment(id, CurrentUser.from(jwt));
    }

    @GetMapping("/submissions/mine")
    public List<SubmissionSummary> myResults(@AuthenticationPrincipal Jwt jwt) {
        return submissionService.myResults(CurrentUser.from(jwt));
    }
}
