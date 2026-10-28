package com.learnhub.assessment.assessment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    public ResponseEntity<AssessmentResponse> create(@Valid @RequestBody CreateAssessmentRequest request,
                                                     @AuthenticationPrincipal Jwt jwt) {
        AssessmentResponse created = assessmentService.create(request, CurrentUser.from(jwt));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /** GET /api/assessments?courseId=6 - @RequestParam reads the query string. */
    @GetMapping
    public List<AssessmentSummary> listForCourse(@RequestParam Long courseId, @AuthenticationPrincipal Jwt jwt) {
        return assessmentService.listForCourse(courseId, CurrentUser.from(jwt));
    }

    @GetMapping("/{id}")
    public AssessmentResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return assessmentService.get(id, CurrentUser.from(jwt));
    }
}
