package com.learnhub.auth.admin;

import com.learnhub.auth.user.Role;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull(message = "role is required") Role role) {

    /** New admins aren't made through the API, so one stolen admin login can't create more. */
    @AssertTrue(message = "role must be STUDENT or INSTRUCTOR")
    public boolean isAssignable() {
        return role != Role.ADMIN;
    }
}
