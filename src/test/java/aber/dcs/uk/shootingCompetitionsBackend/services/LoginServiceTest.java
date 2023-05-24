package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.config.AuthConfig;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.LoginDto;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.RefreshTokenRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtAccessTokenProvider;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtRefreshTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Sql(scripts = {"classpath:sqlScripts/SingleRegisteredUser.sql",
        "classpath:sqlScripts/SingleFakeRefreshToken.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:sqlScripts/TableRefreshTokensCleanup.sql",
        "classpath:sqlScripts/TableUsersCleanup.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginServiceTest {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    AuthenticationManager authenticationManager;
    @SpyBean
    RefreshTokenRepository refreshTokenRepository;

    @SpyBean
    UserRepository userRepository;

    @MockBean
    JwtAccessTokenProvider jwtAccessTokenProvider;
    @MockBean
    JwtRefreshTokenProvider jwtRefreshTokenProvider;

    private AuthJwtTokensManagerService authJwtTokensManagerService;

    private ObjectMapper objectMapper;
    private HttpServletResponse response;

    private LoginService loginService;

    @BeforeEach
    void setUp(){
        authJwtTokensManagerService = new AuthJwtTokensManagerService(
                jwtAccessTokenProvider, jwtRefreshTokenProvider, refreshTokenRepository,
                userRepository);
        objectMapper = new ObjectMapper();
        response = new MockHttpServletResponse();
        loginService = new LoginService(authJwtTokensManagerService,
                authenticationManager, authConfig);
    }

    @Test
    public void loginAttempt_withCorrectCredentialsAndSetCookieToTrue_givesBackAccessTokenInMapAndRefreshTokenInSetCookieHeader() throws AuthenticationException {
        // Arrange
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        String expectedAccessToken = new CredentialsHelper.JwtTokenBuilder().
                addAccessToken().buildToken();
        when(jwtRefreshTokenProvider.issueNewToken(any())).thenReturn(expectedRefreshToken);
        when(jwtAccessTokenProvider.issueNewToken(any())).thenReturn(expectedAccessToken);

        LoginDto loginUser = new CredentialsHelper.LoginCredBuilder().useCorrectCred().build();

        // Act
        Map<String, String> loginResult = loginService.loginAttempt(loginUser, true, response);

        assertTrue(
                StringUtils.hasText(loginResult.get(AuthJwtTokensManagerService.getAccessTokenMapKey()))
        );

        assertEquals(expectedAccessToken, loginResult.get(AuthJwtTokensManagerService.getAccessTokenMapKey()));

        String responseCookie = response.getHeader("Set-Cookie");
        assertNotNull(responseCookie);
        assertTrue(responseCookie.startsWith(
                AuthJwtTokensManagerService.getRefreshTokenMapKey() +
                        '=' + expectedRefreshToken));
    }

    @Test
    public void loginAttempt_withCorrectCredentialsAndSetCookieToFalse_givesBackAccessAndRefreshTokenInMap() throws AuthenticationException {
        // Arrange
        String prefixedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().addTokenPrefix().buildToken();
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        String expectedAccessToken = new CredentialsHelper.JwtTokenBuilder().
                addAccessToken().buildToken();
        when(jwtRefreshTokenProvider.issueNewToken(any())).thenReturn(expectedRefreshToken);
        when(jwtAccessTokenProvider.issueNewToken(any())).thenReturn(expectedAccessToken);

        LoginDto loginUser = new CredentialsHelper.LoginCredBuilder().useCorrectCred().build();

        // Act
        Map<String, String> loginResult = loginService.loginAttempt(loginUser, false, response);

        assertTrue(
                StringUtils.hasText(loginResult.get(AuthJwtTokensManagerService.getRefreshTokenMapKey()))
        );
        assertTrue(
                StringUtils.hasText(loginResult.get(AuthJwtTokensManagerService.getAccessTokenMapKey()))
        );

        assertEquals(prefixedRefreshToken, loginResult.get(AuthJwtTokensManagerService.getRefreshTokenMapKey()));
        assertEquals(expectedAccessToken, loginResult.get(AuthJwtTokensManagerService.getAccessTokenMapKey()));

        String responseCookie = response.getHeader("Set-Cookie");
        assertNull(responseCookie);
    }

    @Test
    public void loginAttempt_withInCorrectCredentialsAndSetCookieToFalse_throwException() throws AuthenticationException {
        // Arrange

        LoginDto loginUser = new CredentialsHelper.LoginCredBuilder().useIncorrectCred().build();


        // Act
        assertThrows(AuthenticationException.class,
                () -> loginService.loginAttempt(loginUser, false, response),
                "Bad credentials");

    }

    @Test
    public void loginAttempt_withInCorrectCredentialsAndSetCookieToTrue_throwException() throws AuthenticationException {
        // Arrange

        LoginDto loginUser = new CredentialsHelper.LoginCredBuilder().useIncorrectCred().build();

        // Act
        assertThrows(AuthenticationException.class,
                () -> loginService.loginAttempt(loginUser, true, response),
                "Bad credentials");
    }

    @Test
    void addAuthorizedUserRefreshTokenAsCookie_withCorrectRefreshToken_setsResponseWithSetCookie() {
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        loginService.addAuthorizedUserRefreshTokenAsCookie(expectedRefreshToken, response);
        String responseCookie = response.getHeader("Set-Cookie");
        assertNotNull(responseCookie);
        assertTrue(responseCookie.startsWith(
                AuthJwtTokensManagerService.getRefreshTokenMapKey() +
                        '=' + expectedRefreshToken));
    }

    @Test
    void addAuthorizedUserRefreshTokenAsCookie_withInCorrectRefreshToken_throwsException() {
        String prefixedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().addTokenPrefix().buildToken();
        assertThrows(AuthenticationException.class,
                () -> loginService.addAuthorizedUserRefreshTokenAsCookie(
                        prefixedRefreshToken, response),
                "Invalid token to set in cookie: " + prefixedRefreshToken);
    }

    @Test
    void addAuthorizedUserRefreshTokenAsCookie_withNull_throwsException() {
        assertThrows(AuthenticationException.class,
                () -> loginService.addAuthorizedUserRefreshTokenAsCookie(
                        null, response),
                "Invalid token to set in cookie: ");
    }

}