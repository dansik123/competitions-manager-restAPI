package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.CustomHttpException;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.RefreshTokenRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.GeneralResponse;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtRefreshTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;

@Service
public class LogoutService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtRefreshTokenProvider jwtRefreshTokenProvider;

    public LogoutService(RefreshTokenRepository refreshTokenRepository,
                         UserRepository userRepository,
                         JwtRefreshTokenProvider jwtRefreshTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtRefreshTokenProvider = jwtRefreshTokenProvider;
    }

    /**
     * Methods logs out user by removing refresh token for client by changing its life span
     * @param request http object which contains header with refresh token
     * @param response http object used to change refreshToken cookie on the client
     * @return General Response with successful log out process
     */
    public GeneralResponse logoutUser(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = //get cookie refreshToken value
                Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals(AuthJwtTokensManagerService.getRefreshTokenMapKey()))
                        .findFirst().map(Cookie::getValue).orElse(null);
        if(refreshToken != null){
            String refreshTokenEmail = jwtRefreshTokenProvider.extractTokenClaims(refreshToken).getSubject();
            UserEntity foundUser = userRepository.findByEmail(refreshTokenEmail).orElseThrow(
                () -> new CustomHttpException(
                    String.format("User with email %s does not exists", refreshTokenEmail),
                    HttpStatus.NOT_FOUND)
                );
            refreshTokenRepository.removeAllUserRefreshTokens(foundUser);
        }
        changeCooke(response);
        return new GeneralResponse("Logout successful");
    }

    /**
     * Method sets new empty refreshToken to response cookie
     * which should immediately expire
     * @param response object used to set new cookie for client
     */
    private void changeCooke(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from(
                        AuthJwtTokensManagerService.getRefreshTokenMapKey(),
                        "") // key & value
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofMillis(0))
                .sameSite("Lax")  // sameSite
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
