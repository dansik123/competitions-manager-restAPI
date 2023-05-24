package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.config.GlobalExceptionHandler;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.LoginDto;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.ErrorResponse;
import aber.dcs.uk.shootingCompetitionsBackend.security.utils.SecurityUtils;
import aber.dcs.uk.shootingCompetitionsBackend.services.AuthJwtTokensManagerService;
import aber.dcs.uk.shootingCompetitionsBackend.services.LoginService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
class LoginControllerTest extends HttpTest{
    @SpyBean
    private UserRepository userRepository;

    @SpyBean
    private AuthJwtTokensManagerService authJwtTokensManagerService;

    @Captor
    ArgumentCaptor<Authentication> authenticationCaptor;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mvcHttp = MockMvcBuilders
                .standaloneSetup(new LoginController(loginService))
                .setControllerAdvice(GlobalExceptionHandler.class).build();
        //any other controller related handles must be added there to run test
    }

    @AfterEach
    void tearDown(){
        clearInvocations(userRepository);
    }

    @Test
    void httpRequestLoginUserTest_with_correctCredentials_gets_200_response() throws Exception {
        LoginDto loginUser = new CredentialsHelper.LoginCredBuilder().useCorrectCred().build();
        httpBodyParsedData = objectMapper.writeValueAsString(loginUser);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/login")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        Map<String, String> actualResponse =
            objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(),
                    typeRef);
        assertTrue("Refresh token must be created",
                StringUtils.hasText(actualResponse.get(AuthJwtTokensManagerService.getRefreshTokenMapKey()))
        );
        assertTrue("Access token must be created",
                StringUtils.hasText(actualResponse.get(AuthJwtTokensManagerService.getAccessTokenMapKey()))
        );
        //check if you can look in authorization variables using ArgumentCaptor

        verify(userRepository, times(2)).findByEmail(loginUser.getEmail());
        verify(authJwtTokensManagerService).generateUserJwtTokens(authenticationCaptor.capture(), eq(false));
        assertEquals(SecurityUtils.extractUserLogin(authenticationCaptor.getValue()), loginUser.getEmail());
        assertNull(authenticationCaptor.getValue().getCredentials());
        assertEquals(String.join(",", authenticationCaptor.getValue().
                        getAuthorities().stream().map(Object::toString).toList()),
                "USER");
    }

    @Test
    public void httpRequestLoginUserTest_with_incorrectCredentials_gets_403_response_and_badCredentialsMessage() throws Exception {
        LoginDto loginUser = new CredentialsHelper.LoginCredBuilder().useIncorrectCred().build();
        httpBodyParsedData = objectMapper.writeValueAsString(loginUser);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/login")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isForbidden()).andReturn();
        ErrorResponse actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        ErrorResponse.class
                );
        assertEquals(actualResponse.getMessage(), "Bad credentials");
        verify(userRepository, times(1)).findByEmail(loginUser.getEmail());
    }
}

