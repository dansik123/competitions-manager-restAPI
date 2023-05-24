package aber.dcs.uk.shootingCompetitionsBackend.responses;

public class ErrorResponse extends BaseResponse {

    private String statusCode;

    public ErrorResponse() {
        super();
    }

    public ErrorResponse(String statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
