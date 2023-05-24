package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.config.GlobalExceptionHandler;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.RefreshTokenRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.ErrorResponse;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtAccessTokenProvider;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtRefreshTokenProvider;
import aber.dcs.uk.shootingCompetitionsBackend.security.utils.SecurityUtils;
import aber.dcs.uk.shootingCompetitionsBackend.services.AuthJwtTokensManagerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.Cookie;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"classpath:sqlScripts/SingleRegisteredUser.sql",
        "classpath:sqlScripts/SingleFakeRefreshToken.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:sqlScripts/TableRefreshTokensCleanup.sql",
        "classpath:sqlScripts/TableUsersCleanup.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
class RefreshControllerTest extends HttpTest {
    @Autowired
    AuthJwtTokensManagerService authJwtTokensManagerService;

    @Captor
    ArgumentCaptor<Authentication> authenticationCaptor;

    @MockBean
    JwtAccessTokenProvider jwtAccessTokenProvider;

    @MockBean
    JwtRefreshTokenProvider jwtRefreshTokenProvider;

    @SpyBean
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mvcHttp = MockMvcBuilders
                .standaloneSetup(new RefreshController(authJwtTokensManagerService))
                .setControllerAdvice(GlobalExceptionHandler.class).build();
        //any other controller related handles must be added there to run test
    }

    @AfterEach
    void tearDown() {
        clearInvocations(refreshTokenRepository);
    }

    @Test
    void httpRequestRefreshTokenTest_with_correctTokenInRequestBody_gets_200_response() throws Exception {
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        String prefixedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().addTokenPrefix().buildToken();
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        String expectedAccessToken = new CredentialsHelper.JwtTokenBuilder().
                addAccessToken().buildToken();
        Map<String, String> refreshBody = Map.of(
                AuthJwtTokensManagerService.getRefreshTokenMapKey(),
                prefixedRefreshToken);
        httpBodyParsedData = objectMapper.writeValueAsString(refreshBody);

        DefaultClaims fakeGeneratedClaims = new DefaultClaims();
        fakeGeneratedClaims.setSubject("fakeEmail@nano.com");
        fakeGeneratedClaims.setIssuer("fake_test_issuer");
        when(jwtAccessTokenProvider.issueNewToken(any())).thenReturn(expectedAccessToken);
        when(jwtRefreshTokenProvider.extractTokenClaims(expectedRefreshToken)).thenReturn(fakeGeneratedClaims);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/refresh")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        Map<String, String> actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        typeRef);
        assertTrue("Response must have access Token",
                actualResponse.containsKey(AuthJwtTokensManagerService.getAccessTokenMapKey()));
        String responseAccessToken = actualResponse.get(AuthJwtTokensManagerService.getAccessTokenMapKey());
        assertEquals(expectedAccessToken, responseAccessToken);

        verify(jwtAccessTokenProvider).issueNewToken(authenticationCaptor.capture());

        assertEquals(SecurityUtils.extractUserLogin(authenticationCaptor.getValue()), fakeGeneratedClaims.getSubject());
        assertEquals(CredentialsHelper.getCorrectEncodedPassword(), authenticationCaptor.getValue().getCredentials());
        assertEquals(String.join(",", authenticationCaptor.getValue().
                        getAuthorities().stream().map(Object::toString).toList()),
                "USER");

        verify(refreshTokenRepository).findByRefreshToken(expectedRefreshToken);
    }

    @Test
    void httpRequestRefreshTokenTest_with_correctTokenInCookie_gets_200_response() throws Exception {
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        String expectedAccessToken = new CredentialsHelper.JwtTokenBuilder().
                addAccessToken().buildToken();
        //String fakeGeneratedJwtNoPrefix = CredentialsHelper.getFakeRefreshToken();
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        httpBodyParsedData = "{}";

        Cookie cookie = new Cookie(
                AuthJwtTokensManagerService.getRefreshTokenMapKey(), expectedRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge((int) Duration.ofDays(5).toSeconds());

        DefaultClaims fakeGeneratedClaims = new DefaultClaims();
        fakeGeneratedClaims.setSubject("fakeEmail@nano.com");
        fakeGeneratedClaims.setIssuer("fake_test_issuer");

        when(jwtAccessTokenProvider.issueNewToken(any())).thenReturn(expectedAccessToken);
        when(jwtRefreshTokenProvider.extractTokenClaims(expectedRefreshToken)).thenReturn(fakeGeneratedClaims);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/refresh")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData)
                        .cookie(cookie);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        Map<String, String> actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        typeRef);
        assertTrue("Response must have access Token",
                actualResponse.containsKey(AuthJwtTokensManagerService.getAccessTokenMapKey()));
        String responseAccessToken = actualResponse.get(AuthJwtTokensManagerService.getAccessTokenMapKey());
        assertEquals(expectedAccessToken, responseAccessToken);

        verify(jwtAccessTokenProvider).issueNewToken(authenticationCaptor.capture());

        assertEquals(SecurityUtils.extractUserLogin(authenticationCaptor.getValue()), fakeGeneratedClaims.getSubject());
        assertEquals(CredentialsHelper.getCorrectEncodedPassword(), authenticationCaptor.getValue().getCredentials());
        assertEquals(String.join(",", authenticationCaptor.getValue().
                        getAuthorities().stream().map(Object::toString).toList()),
                "USER");

        verify(refreshTokenRepository).findByRefreshToken(expectedRefreshToken);
    }

    @Test
    void httpRequestRefreshTokenTest_with_requestWithoutBodyAndCookie_gets_403_response() throws Exception {
        httpBodyParsedData = "{}";

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/refresh")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isForbidden()).andReturn();

        ErrorResponse actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        ErrorResponse.class);
        String expectedErrorMessage = "No cookie set and no body refreshToken for " +
        "refresh. Please login again";
        String responseMessage = actualResponse.getMessage();

        assertEquals(expectedErrorMessage, responseMessage);
        verify(jwtAccessTokenProvider, times(0)).issueNewToken(any());
        verify(jwtRefreshTokenProvider, times(0)).extractTokenClaims(any());
    }

    @Test
    void httpRequestRefreshTokenTest_with_correctTokenWithoutBearerPrefixInRequestBody_gets_403_response() throws Exception {
        String expectedRefreshToken = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().buildToken();
        Map<String, String> refreshBody = Map.of(
                AuthJwtTokensManagerService.getRefreshTokenMapKey(), expectedRefreshToken);
        httpBodyParsedData = objectMapper.writeValueAsString(refreshBody);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/refresh")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isForbidden()).andReturn();

        ErrorResponse actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        ErrorResponse.class);

        String expectedErrorMessage = "Refresh token doesn't contain 'Bearer ' " +
                "prefix or is just null";
        String responseMessage = actualResponse.getMessage();

        assertEquals(expectedErrorMessage, responseMessage);
        verify(jwtAccessTokenProvider, times(0)).issueNewToken(any());
        verify(jwtRefreshTokenProvider, times(0)).extractTokenClaims(any());
    }
    @Test
    void httpRequestRefreshTokenTest_with_validTokenButNotSavedInDatabase_gets_403_response() throws Exception {
        String prefixedRefreshTokenNotInDb = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().addTokenPrefix().addNotInDBSuffix().buildToken();
        String expectedRefreshTokenNotInDb = new CredentialsHelper.JwtTokenBuilder().
                addRefreshToken().addNotInDBSuffix().buildToken();
        Map<String, String> refreshBody = Map.of(
                AuthJwtTokensManagerService.getRefreshTokenMapKey(), prefixedRefreshTokenNotInDb);
        httpBodyParsedData = objectMapper.writeValueAsString(refreshBody);

        DefaultClaims fakeGeneratedClaims = new DefaultClaims();
        fakeGeneratedClaims.setSubject("fakeEmail@nano.com");
        fakeGeneratedClaims.setIssuer("fake_test_issuer");

        when(jwtRefreshTokenProvider.extractTokenClaims(expectedRefreshTokenNotInDb)).thenReturn(fakeGeneratedClaims);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/refresh")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);


        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isForbidden()).andReturn();

        ErrorResponse actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        ErrorResponse.class);
        String expectedErrorMessage = "Refresh token used after logout token(please login again)";
        assertEquals(expectedErrorMessage, actualResponse.getMessage());

        verify(jwtAccessTokenProvider, times(0)).issueNewToken(any());

        verify(refreshTokenRepository).findByRefreshToken(expectedRefreshTokenNotInDb);
    }

}