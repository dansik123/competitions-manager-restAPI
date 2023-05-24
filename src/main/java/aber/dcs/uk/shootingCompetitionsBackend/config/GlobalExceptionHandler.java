package aber.dcs.uk.shootingCompetitionsBackend.config;

import aber.dcs.uk.shootingCompetitionsBackend.exceptions.CustomHttpException;
import aber.dcs.uk.shootingCompetitionsBackend.responses.ErrorResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST endpoints error handlers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    public final static String FORBIDDEN_AUTH_PREFIX = "Auth error:";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ErrorResponse apiError = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), message);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNoSupportException(HttpMediaTypeNotSupportedException ex) {
        ErrorResponse apiError = new ErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE.toString(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtParsingException(JwtException ex) {
        String message = ex.getMessage();
        ErrorResponse apiError = new ErrorResponse(HttpStatus.FORBIDDEN.toString(), message);
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleLoginAuthenticationProblem(AuthenticationException ex) {
        String message = ex.getMessage();
        ErrorResponse apiError = new ErrorResponse(HttpStatus.FORBIDDEN.toString(), message);
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedError(AccessDeniedException ex) {
        String message = ex.getMessage();
        ErrorResponse apiError = new ErrorResponse(HttpStatus.UNAUTHORIZED.toString(), message);
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomHttpException.class)
    public ResponseEntity<ErrorResponse> handleCustomHttpErrors(CustomHttpException ex) {
        String message = ex.getMessage();
        HttpStatus status = ex.getStatus();
        ErrorResponse apiError = new ErrorResponse(status.toString(), message);
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOFileException(IOException ex) {
        String prefix = "IO file read problem:";
        ErrorResponse apiError = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), prefix + ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
