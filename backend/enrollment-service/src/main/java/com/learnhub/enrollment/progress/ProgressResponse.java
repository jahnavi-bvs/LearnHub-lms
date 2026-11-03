package com.learnhub.enrollment.progress;

import java.util.List;
import java.util.Set;

/**
 * A student's progress through one course.
 * nextModuleId is the first module not yet completed: where "Continue learning" should go.
 * It's null when there's nothing left to do.
 */
public record ProgressResponse(Long enrollmentId, Long courseId, List<Long> completedModuleIds, int completedCount,
                               int totalModules, int percentage, Long nextModuleId) {

    /** Calculated from the course's current modules, so completions of deleted modules don't count. */
    static ProgressResponse of(Long enrollmentId, Long courseId, List<Long> moduleIdsInOrder, Set<Long> completed) {
        List<Long> done = moduleIdsInOrder.stream().filter(completed::contains).toList();
        Long next = moduleIdsInOrder.stream().filter(id -> !completed.contains(id)).findFirst().orElse(null);
        int total = moduleIdsInOrder.size();
        int percentage = total == 0 ? 0 : (int) Math.round(done.size() * 100.0 / total);
        return new ProgressResponse(enrollmentId, courseId, done, done.size(), total, percentage, next);
    }
}
