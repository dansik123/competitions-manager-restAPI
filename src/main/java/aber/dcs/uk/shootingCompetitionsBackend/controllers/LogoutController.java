package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.responses.GeneralResponse;
import aber.dcs.uk.shootingCompetitionsBackend.services.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/logout")
public class LogoutController {
    private final LogoutService logoutService;

    public LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    //TODO: log out should also need to be performed on request where user sends refresh token in
    // request body
    /**
     * HTTP POST method to log out user
     * @param request HTTP request object must contain refresh Token cookie
     * @param response HTTP response object used to change refreshToken cookie life span to 0
     * @return OK response with information about successful log out
     */
    @PostMapping
    public ResponseEntity<GeneralResponse> logoutUser(
            HttpServletRequest request,
            HttpServletResponse response)
    {
        GeneralResponse responseBody = logoutService.logoutUser(request, response);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
