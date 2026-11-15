package com.practicantes.plataforma.infrastructure.web;

import com.practicantes.plataforma.application.exception.DuplicateApplicantEmailException;
import com.practicantes.plataforma.application.exception.ApplicantNotFoundException;
import com.practicantes.plataforma.application.exception.CvNotFoundException;
import com.practicantes.plataforma.application.exception.InvalidApplicantStatusException;
import com.practicantes.plataforma.application.exception.InvalidCvException;
import com.practicantes.plataforma.application.exception.InvalidCredentialsException;
import com.practicantes.plataforma.application.exception.InvalidRefreshTokenException;
import com.practicantes.plataforma.application.exception.MissingStatusCommentException;
import com.practicantes.plataforma.application.exception.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

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

    @ExceptionHandler(CvNotFoundException.class)
    ProblemDetail handleCvNotFound(CvNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidApplicantStatusException.class)
    ProblemDetail handleInvalidStatus(InvalidApplicantStatusException exception) {
        return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MissingStatusCommentException.class)
    ProblemDetail handleMissingStatusComment(MissingStatusCommentException exception) {
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

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ProblemDetail handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception) {
        String accepted = exception.getSupportedMediaTypes().isEmpty()
                ? ""
                : " Formatos aceptados: " + exception.getSupportedMediaTypes();
        return problem(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "El formato de la solicitud no es compatible." + accepted);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    ProblemDetail handleMissingPart(MissingServletRequestPartException exception) {
        return problem(HttpStatus.BAD_REQUEST,
                "Falta la parte requerida de la solicitud: " + exception.getRequestPartName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleUnreadableMessage(HttpMessageNotReadableException exception) {
        return problem(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no tiene un formato válido");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return problem(HttpStatus.BAD_REQUEST,
                "El parámetro '" + exception.getName() + "' tiene un formato inválido");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
        return problem(HttpStatus.BAD_REQUEST, exception.getMessage() == null
                ? "La solicitud contiene datos inválidos"
                : exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("Error inesperado procesando {} {}", request.getMethod(), request.getRequestURI(), exception);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno. Intenta nuevamente más tarde");
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

