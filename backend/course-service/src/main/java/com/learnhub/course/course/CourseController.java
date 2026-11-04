package com.learnhub.course.course;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponse> listPublished() {
        return courseService.listPublished();
    }

    @GetMapping("/mine") // a fixed path beats the "/{id}" pattern below
    public List<CourseResponse> listMine(@AuthenticationPrincipal Jwt jwt) {
        return courseService.listMine(CurrentUser.from(jwt));
    }

    @GetMapping("/all")
    public List<CourseResponse> listAll() {
        return courseService.listAll();
    }

    @PutMapping("/{id}/unpublish")
    public CourseResponse unpublish(@PathVariable Long id) {
        return courseService.unpublish(id);
    }

    /** Public endpoint: jwt is null when no token was sent. */
    @GetMapping("/{id}")
    public CourseResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return courseService.get(id, jwt == null ? null : CurrentUser.from(jwt));
    }

    /** 201 Created, with a Location header pointing at the new course. */
    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        CourseResponse created = courseService.create(request, CurrentUser.from(jwt));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable Long id, @Valid @RequestBody CourseRequest request,
                                 @AuthenticationPrincipal Jwt jwt) {
        return courseService.update(id, request, CurrentUser.from(jwt));
    }

    /** 204 No Content: success, nothing to return. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        courseService.delete(id, CurrentUser.from(jwt));
        return ResponseEntity.noContent().build();
    }
}
