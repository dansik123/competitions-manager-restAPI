package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.LoginDto;
import aber.dcs.uk.shootingCompetitionsBackend.services.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/login")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * HTTP Post method to log in user
     * @param loginUser login in user credentials
     * @param httpClient client program which send request (browser or other)
     *                   other clients will have
     * @param response response object to sent new refreshToken cookie if needed
     * @return If client is browser the response will give back accessToken in request body and refresh token as cookie
     * otherwise both tokens will get send in request body
     */
    @PostMapping
    public ResponseEntity<Map<String,String>> loginUser(
            @Valid @RequestBody LoginDto loginUser,
            @RequestParam(name="client", defaultValue = "other") String httpClient,
            HttpServletResponse response){
        Map<String,String> loginResponse;
        if(httpClient.equals("browser")) {
            loginResponse = loginService.loginAttempt(loginUser, true, response);
        }else {
            loginResponse = loginService.loginAttempt(loginUser, false, response);
        }
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }


}
