package com.learnhub.course.module;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** JSON body of PUT /api/courses/{id}/modules/order: every module id, in the new order. */
public record ReorderRequest(

        @NotEmpty(message = "Module ids are required")
        List<@NotNull(message = "Module ids cannot be empty") Long> moduleIds
) {
}
