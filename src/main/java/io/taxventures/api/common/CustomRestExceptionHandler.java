package io.taxventures.api.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
        Optional<FieldError> error = ex.getBindingResult().getFieldErrors().stream().findFirst();
        if (error.isEmpty()) {
            return ResponseEntity.status(400).build();
        }
        String errorMessage = error.get().getField() + ": " + error.get().getDefaultMessage();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.status(400).body(apiError);
    }
}
