package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.LoginDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.RefreshTokenEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.JwtAuthenticationException;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.RefreshTokenRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtAccessTokenProvider;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtRefreshTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Sql(scripts = {"classpath:sqlScripts/SingleRegisteredUser.sql",
        "classpath:sqlScripts/SingleFakeRefreshToken.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:sqlScripts/TableRefreshTokensCleanup.sql",
        "classpath:sqlScripts/TableUsersCleanup.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
class AuthJwtTokensManagerServiceTest {
    @SpyBean
    AuthJwtTokensManagerService authJwtTokensManagerServiceTester;
    @MockBean
    JwtAccessTokenProvider jwtAccessTokenProvider;
    @MockBean
    JwtRefreshTokenProvider jwtRefreshTokenProvider;
    @SpyBean
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserRepository userRepository;

    @Captor
    ArgumentCaptor<RefreshTokenEntity> refreshTokenEntityArgumentCaptor;

    LoginDto correctUserCred = new CredentialsHelper.LoginCredBuilder().useCorrectCred().build();

    @BeforeEach
    void setUp() {
        authJwtTokensManagerServiceTester = new AuthJwtTokensManagerService(
                jwtAccessTokenProvider, jwtRefreshTokenProvider,
                refreshTokenRepository, userRepository
        );
    }

    @AfterEach
    void tearDown(){
        clearInvocations(refreshTokenRepository);
    }

    @Test
    void generateUserJwtTokens_withGeneratedRefreshTokenCookie_ReturnsTwoTokensWhereRefreshTokenDoesNotHaveBearerPrefix() {
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        String expectedAccessToken = new CredentialsHelper.JwtTokenBuilder().
                addAccessToken().buildToken();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                correctUserCred.getEmail(),
                correctUserCred.getPassword(),
                CredentialsHelper.getDatabaseRegisteredUserEntity().getAuthorities()
        );

        when(jwtRefreshTokenProvider.issueNewToken(auth)).thenReturn(expectedRefreshToken);
        when(jwtAccessTokenProvider.issueNewToken(auth)).thenReturn(expectedAccessToken);

        Map<String, String> actualTokensMap = authJwtTokensManagerServiceTester.generateUserJwtTokens(
            auth, true
        );
        Map<String, String> expectedTokensMap = Map.of(
                AuthJwtTokensManagerService.getRefreshTokenMapKey(),
                expectedRefreshToken,
                AuthJwtTokensManagerService.getAccessTokenMapKey(),
                expectedAccessToken
        );
        for(String actualMapToken: actualTokensMap.keySet()){
            assertEquals(expectedTokensMap.get(actualMapToken), actualTokensMap.get(actualMapToken));
        }
        verify(refreshTokenRepository).save(refreshTokenEntityArgumentCaptor.capture());
        assertEquals(1, refreshTokenEntityArgumentCaptor.getValue().getOwner().getId());
        assertEquals(expectedRefreshToken,
                refreshTokenEntityArgumentCaptor.getValue().getRefreshToken());
    }

    @Test
    void generateUserJwtTokens_withGeneratedRefreshTokenCookie_ReturnsTwoTokensWhereRefreshTokenDoesHaveBearerPrefix() {
        String prefixedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().addTokenPrefix().buildToken();
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        String expectedAccessToken = new CredentialsHelper.JwtTokenBuilder().
                addAccessToken().buildToken();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                correctUserCred.getEmail(),
                correctUserCred.getPassword(),
                CredentialsHelper.getDatabaseRegisteredUserEntity().getAuthorities()
        );

        when(jwtRefreshTokenProvider.issueNewToken(auth)).thenReturn(expectedRefreshToken);
        when(jwtAccessTokenProvider.issueNewToken(auth)).thenReturn(expectedAccessToken);

        Map<String, String> actualTokensMap = authJwtTokensManagerServiceTester.generateUserJwtTokens(
                auth, false
        );
        Map<String, String> expectedTokensMap = Map.of(
                AuthJwtTokensManagerService.getRefreshTokenMapKey(),
                prefixedRefreshToken,
                AuthJwtTokensManagerService.getAccessTokenMapKey(),
                expectedAccessToken
        );
        for(String actualMapToken: actualTokensMap.keySet()){
            assertEquals(expectedTokensMap.get(actualMapToken), actualTokensMap.get(actualMapToken));
        }
        verify(refreshTokenRepository).save(refreshTokenEntityArgumentCaptor.capture());
        assertEquals(1, refreshTokenEntityArgumentCaptor.getValue().getOwner().getId());
        assertEquals(expectedRefreshToken, refreshTokenEntityArgumentCaptor.getValue().getRefreshToken());
    }


    @Test
    void issueAccessTokenFromCookieOrRequestBody_withEmptyRequestBodyAndNoCookies_ThrowException() {
        HttpServletRequest request =
                MockMvcRequestBuilders
                        .get("/v1/api/refresh")
                        .contentType(APPLICATION_JSON)
                        .content("{}").buildRequest(new MockServletContext());

        assertThrowsExactly(JwtAuthenticationException.class, () ->
            authJwtTokensManagerServiceTester.issueAccessTokenFromCookieOrRequestBody(request, Map.of()),
    "No cookie set and no body refreshToken for " +
            "refresh. Please login again"
        );
    }

    @Test
    void issueAccessTokenFromCookieOrRequestBody_withEmptyRequestBodyAndRefreshCookie_calls_method_with_correctly_parsed_token() {
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        String expectedAccessToken = new CredentialsHelper.JwtTokenBuilder().
                addAccessToken().buildToken();
        Cookie cookie = new Cookie(
                AuthJwtTokensManagerService.getRefreshTokenMapKey(),
                expectedRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge((int) Duration.ofDays(5).toSeconds());

        when(jwtAccessTokenProvider.issueNewToken(any())).thenReturn(expectedAccessToken);
        when(jwtRefreshTokenProvider.extractTokenClaims(expectedRefreshToken)).thenReturn(
                new DefaultClaims(Map.of("sub", correctUserCred.getEmail())));

        HttpServletRequest request =
                MockMvcRequestBuilders
                        .get("/v1/api/refresh")
                        .contentType(APPLICATION_JSON)
                        .cookie(cookie)
                        .content("{}").buildRequest(new MockServletContext());

        Map<String, String> actualAccessTokenMap =
                authJwtTokensManagerServiceTester.issueAccessTokenFromCookieOrRequestBody(request, Map.of());
        assertTrue(actualAccessTokenMap.containsKey(AuthJwtTokensManagerService.getAccessTokenMapKey()));
        assertEquals(expectedAccessToken, actualAccessTokenMap.get(AuthJwtTokensManagerService.getAccessTokenMapKey()));
    }

    @Test
    void issueAccessTokenFromCookieOrRequestBody_withRequestBodyAndNoRefreshCookie_calls_method_with_correctly_parsed_token()
            throws JsonProcessingException {
        String prefixedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().addTokenPrefix().buildToken();
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        String expectedAccessToken = new CredentialsHelper.JwtTokenBuilder().
                addAccessToken().buildToken();
        Map<String, String> requestBodyMap = Map.of(
                AuthJwtTokensManagerService.getRefreshTokenMapKey(),
                prefixedRefreshToken);
        String requestBody = new ObjectMapper().writeValueAsString(requestBodyMap);

        when(jwtAccessTokenProvider.issueNewToken(any())).thenReturn(expectedAccessToken);
        when(jwtRefreshTokenProvider.extractTokenClaims(expectedRefreshToken)).thenReturn(
                new DefaultClaims(Map.of("sub", correctUserCred.getEmail())));

        HttpServletRequest request =
                MockMvcRequestBuilders
                        .get("/v1/api/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody).buildRequest(new MockServletContext());

        Map<String, String> actualAccessTokenMap =
                authJwtTokensManagerServiceTester.issueAccessTokenFromCookieOrRequestBody(request, requestBodyMap);
        assertTrue(actualAccessTokenMap.containsKey(AuthJwtTokensManagerService.getAccessTokenMapKey()));
        assertEquals(expectedAccessToken, actualAccessTokenMap.get(AuthJwtTokensManagerService.getAccessTokenMapKey()));
    }

    @Test
    void issueNewAccessTokenFromRefreshToken_withNullRefreshToken_throwException() {
        assertThrowsExactly(JwtAuthenticationException.class, () ->
                        authJwtTokensManagerServiceTester.issueNewAccessTokenFromRefreshToken(null),
                "Refresh token doesn't contain 'Bearer ' prefix or is just null"
        );
    }

    @Test
    void issueNewAccessTokenFromRefreshToken_RefreshTokenNotInDatabase_throwException() {
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().addNotInDBSuffix().buildToken();
        assertThrowsExactly(AuthenticationServiceException.class, () ->
            authJwtTokensManagerServiceTester.issueNewAccessTokenFromRefreshToken(expectedRefreshToken),
    "Refresh token used after logout token(please login again)"
        );
    }

    @Test
    void issueNewAccessTokenFromRefreshToken_claimSubjectEmailDoesNotMatchDatabaseEmail_throwException() {
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        when(jwtRefreshTokenProvider.extractTokenClaims(expectedRefreshToken)).thenReturn(
                new DefaultClaims(Map.of("sub", correctUserCred.getEmail().substring(0,5))));
        assertThrowsExactly(AuthenticationServiceException.class, () ->
                        authJwtTokensManagerServiceTester.issueNewAccessTokenFromRefreshToken(expectedRefreshToken),
                "Token subject doesn't match with user's email"
        );
    }

    @Test
    void issueNewAccessTokenFromRefreshToken_withOkayRefreshTokenContainsUserEmailMatchItInDatabase_returnsExpectedAccessToken() {
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        String expectedAccessToken = new CredentialsHelper.JwtTokenBuilder().
                addAccessToken().buildToken();
        when(jwtAccessTokenProvider.issueNewToken(any())).thenReturn(expectedAccessToken);
        when(jwtRefreshTokenProvider.extractTokenClaims(expectedRefreshToken)).thenReturn(
                new DefaultClaims(Map.of("sub", "fakeEmail@nano.com")));

        Map<String, String> actualAccessTokenMap =
                authJwtTokensManagerServiceTester.issueNewAccessTokenFromRefreshToken(expectedRefreshToken);

        assertTrue(actualAccessTokenMap.containsKey(AuthJwtTokensManagerService.getAccessTokenMapKey()));
        assertEquals(expectedAccessToken, actualAccessTokenMap.get(AuthJwtTokensManagerService.getAccessTokenMapKey()));
    }

}