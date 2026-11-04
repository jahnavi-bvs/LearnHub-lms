package com.learnhub.auth.admin;

import jakarta.validation.constraints.NotNull;

public record UpdateActiveRequest(@NotNull(message = "active is required") Boolean active) {
}
