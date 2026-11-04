package com.learnhub.enrollment.enrollment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentController(EnrollmentService enrollmentService, EnrollmentRepository enrollmentRepository) {
        this.enrollmentService = enrollmentService;
        this.enrollmentRepository = enrollmentRepository;
    }

    /** Admin only (see SecurityConfig): one read with no rules, so no service method is needed. */
    @GetMapping("/stats/by-course")
    public List<CourseEnrollmentCount> countByCourse() {
        return enrollmentRepository.countByCourse();
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollRequest request,
                                                     @AuthenticationPrincipal Jwt jwt) {
        EnrollmentResponse created = enrollmentService.enroll(request, CurrentUser.from(jwt));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/mine")
    public List<EnrollmentResponse> listMine(@AuthenticationPrincipal Jwt jwt) {
        return enrollmentService.listMine(CurrentUser.from(jwt));
    }

    @GetMapping("/course/{courseId}")
    public List<EnrollmentResponse> listForCourse(@PathVariable Long courseId, @AuthenticationPrincipal Jwt jwt) {
        return enrollmentService.listForCourse(courseId, CurrentUser.from(jwt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        enrollmentService.cancel(id, CurrentUser.from(jwt));
        return ResponseEntity.noContent().build();
    }
}
