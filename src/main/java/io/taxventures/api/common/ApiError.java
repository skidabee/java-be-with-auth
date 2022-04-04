package io.taxventures.api.common;

import org.springframework.http.HttpStatus;

public record ApiError(HttpStatus status, String message) {
}
