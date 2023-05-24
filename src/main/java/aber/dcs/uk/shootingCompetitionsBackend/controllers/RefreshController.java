package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.services.AuthJwtTokensManagerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/refresh")
public class RefreshController {
    private final AuthJwtTokensManagerService authJwtTokensManager;

    public RefreshController(AuthJwtTokensManagerService authJwtTokensManager) {
        this.authJwtTokensManager = authJwtTokensManager;
    }

    /**
     * HTTP Get method, it generates new access token using the refresh token send in the request
     * (refreshToken must be in request body[key:value] pair or in request cookie
     * @param refreshMap map with expected refreshToken key
     * @param request request data contains refreshToken in headers defined as cookie
     * @return key, value pair result which contains newly created access token or
     * error response if something went wrong
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> refreshAccessToken(
            @RequestBody(required = false) Map<String, String> refreshMap,
            HttpServletRequest request){
        return new ResponseEntity<>(
                authJwtTokensManager.issueAccessTokenFromCookieOrRequestBody(request, refreshMap),
                HttpStatus.OK);
    }
}
