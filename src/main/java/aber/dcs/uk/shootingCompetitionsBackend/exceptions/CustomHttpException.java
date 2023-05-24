package aber.dcs.uk.shootingCompetitionsBackend.exceptions;

import org.springframework.http.HttpStatus;

public class CustomHttpException extends RuntimeException{
    private final HttpStatus status;

    public CustomHttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
