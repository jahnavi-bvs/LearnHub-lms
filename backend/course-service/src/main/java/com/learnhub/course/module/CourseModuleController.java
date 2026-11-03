package com.learnhub.course.module;

import com.learnhub.course.course.CurrentUser;
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
@RequestMapping("/api/courses/{courseId}/modules")
public class CourseModuleController {

    private final CourseModuleService moduleService;

    public CourseModuleController(CourseModuleService moduleService) {
        this.moduleService = moduleService;
    }

    /** Public: jwt is null when no token was sent. */
    @GetMapping
    public List<ModuleSummary> list(@PathVariable Long courseId, @AuthenticationPrincipal Jwt jwt) {
        return moduleService.list(courseId, jwt == null ? null : CurrentUser.from(jwt));
    }

    @GetMapping("/{moduleId}")
    public ModuleResponse get(@PathVariable Long courseId, @PathVariable Long moduleId,
                              @AuthenticationPrincipal Jwt jwt) {
        return moduleService.get(courseId, moduleId, CurrentUser.from(jwt));
    }

    @PostMapping
    public ResponseEntity<ModuleResponse> create(@PathVariable Long courseId, @Valid @RequestBody ModuleRequest request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        ModuleResponse created = moduleService.create(courseId, request, CurrentUser.from(jwt));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/order") // a fixed path beats the "/{moduleId}" pattern below
    public List<ModuleSummary> reorder(@PathVariable Long courseId, @Valid @RequestBody ReorderRequest request,
                                       @AuthenticationPrincipal Jwt jwt) {
        return moduleService.reorder(courseId, request, CurrentUser.from(jwt));
    }

    @PutMapping("/{moduleId}")
    public ModuleResponse update(@PathVariable Long courseId, @PathVariable Long moduleId,
                                 @Valid @RequestBody ModuleRequest request, @AuthenticationPrincipal Jwt jwt) {
        return moduleService.update(courseId, moduleId, request, CurrentUser.from(jwt));
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> delete(@PathVariable Long courseId, @PathVariable Long moduleId,
                                       @AuthenticationPrincipal Jwt jwt) {
        moduleService.delete(courseId, moduleId, CurrentUser.from(jwt));
        return ResponseEntity.noContent().build();
    }
}
