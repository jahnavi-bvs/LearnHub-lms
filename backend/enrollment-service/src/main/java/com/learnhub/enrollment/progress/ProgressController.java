package com.learnhub.enrollment.progress;

import com.learnhub.enrollment.enrollment.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/mine/progress")
    public List<ProgressResponse> listMine(@AuthenticationPrincipal Jwt jwt) {
        return progressService.listMine(CurrentUser.from(jwt));
    }

    /** Mark as complete. PUT because repeating it has the same effect as doing it once. */
    @PutMapping("/{id}/completed-modules/{moduleId}")
    public ProgressResponse markComplete(@PathVariable Long id, @PathVariable Long moduleId,
                                         @AuthenticationPrincipal Jwt jwt) {
        return progressService.markComplete(id, moduleId, CurrentUser.from(jwt));
    }

    @DeleteMapping("/{id}/completed-modules/{moduleId}")
    public ProgressResponse markIncomplete(@PathVariable Long id, @PathVariable Long moduleId,
                                           @AuthenticationPrincipal Jwt jwt) {
        return progressService.markIncomplete(id, moduleId, CurrentUser.from(jwt));
    }
}
