package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.config.AuthConfig;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.LoginDto;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtRefreshTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class LoginService {
    private final AuthJwtTokensManagerService authJwtTokensManager;
    private final AuthenticationManager authenticationManager;
    private final AuthConfig authConfig;

    public LoginService(
            AuthJwtTokensManagerService authJwtTokensManager,
            AuthenticationManager authenticationManager,
            AuthConfig authConfig) {
        this.authJwtTokensManager = authJwtTokensManager;
        this.authenticationManager = authenticationManager;
        this.authConfig = authConfig;
    }

    /**
     * Method attempts to authenticate user based on it's credentials
     * @param loginUser users credentials(user's email and password)
     * @return Map with refreshToken and accessToken
     * @throws AuthenticationException with information about failed authentication or JWT token generation
     */
    public Map<String,String> loginAttempt(LoginDto loginUser,
                                           boolean refreshTokenAsCookie,
                                           HttpServletResponse response) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginUser.getEmail(), loginUser.getPassword())
        );
        Map<String, String> responseJwtToken =
                authJwtTokensManager.generateUserJwtTokens(authentication, refreshTokenAsCookie);
        if(refreshTokenAsCookie){
            String refreshToken = responseJwtToken.remove(AuthJwtTokensManagerService.getRefreshTokenMapKey());
            this.addAuthorizedUserRefreshTokenAsCookie(refreshToken, response);
        }
        return responseJwtToken;
    }

    /**
     * Method adds refreshToken HTTP only Cookie for browser
     * @param refreshToken String with refresh token(can't contain
     * @param response HTTP response object to which Cookie will get attached
     */
    public void addAuthorizedUserRefreshTokenAsCookie(String refreshToken, HttpServletResponse response){
        String forbiddenCookieTokenPrefix = JwtRefreshTokenProvider.getTokenPrefix();
        if(refreshToken != null && !refreshToken.startsWith(forbiddenCookieTokenPrefix)){
            ResponseCookie cookie = ResponseCookie.from(
                            AuthJwtTokensManagerService.getRefreshTokenMapKey(),
                            refreshToken) // key & value
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(Duration.ofMillis(authConfig.getRefreshTokenValidTime()))
                    .sameSite("Lax")  // sameSite
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return;
        }

        throw new AuthenticationServiceException(
                String.format("Invalid token to set in cookie: '%s'", refreshToken));
    }
    //TODO: add CRON SCHEDULER to remove daily the expired refresh tokens
}
