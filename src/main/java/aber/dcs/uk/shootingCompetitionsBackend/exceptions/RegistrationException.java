package aber.dcs.uk.shootingCompetitionsBackend.exceptions;

public class RegistrationException extends RuntimeException{
    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
