package com.practicantes.plataforma.infrastructure.web;

import com.practicantes.plataforma.application.exception.DuplicateApplicantEmailException;
import com.practicantes.plataforma.application.exception.ApplicantNotFoundException;
import com.practicantes.plataforma.application.exception.InvalidApplicantStatusException;
import com.practicantes.plataforma.application.exception.InvalidCvException;
import com.practicantes.plataforma.application.exception.InvalidCredentialsException;
import com.practicantes.plataforma.application.exception.InvalidRefreshTokenException;
import com.practicantes.plataforma.application.exception.TooManyRequestsException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DuplicateApplicantEmailException.class)
    ProblemDetail handleDuplicateEmail(DuplicateApplicantEmailException exception) {
        return problem(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidCvException.class)
    ProblemDetail handleInvalidCv(InvalidCvException exception) {
        return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ApplicantNotFoundException.class)
    ProblemDetail handleNotFound(ApplicantNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidApplicantStatusException.class)
    ProblemDetail handleInvalidStatus(InvalidApplicantStatusException exception) {
        return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ProblemDetail handleInvalidCredentials(InvalidCredentialsException exception) {
        return problem(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    ProblemDetail handleInvalidRefreshToken(InvalidRefreshTokenException exception) {
        return problem(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    ProblemDetail handleTooManyRequests(TooManyRequestsException exception) {
        return problem(HttpStatus.TOO_MANY_REQUESTS, exception.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ProblemDetail handleMaxUploadSize(MaxUploadSizeExceededException exception) {
        return problem(HttpStatus.PAYLOAD_TOO_LARGE, "El archivo no puede superar 5 MB");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        return validationProblem(exception.getBindingResult());
    }

    @ExceptionHandler(BindException.class)
    ProblemDetail handleBinding(BindException exception) {
        return validationProblem(exception.getBindingResult());
    }

    private ProblemDetail validationProblem(BindingResult bindingResult) {
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "Hay campos inválidos en la solicitud");
        Map<String, String> errors = new LinkedHashMap<>();
        bindingResult.getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        problem.setProperty("errors", errors);
        return problem;
    }

    private ProblemDetail problem(HttpStatus status, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        return problem;
    }
}

