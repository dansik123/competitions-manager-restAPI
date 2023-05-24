package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.config.GlobalExceptionHandler;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.RegisterUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.ErrorResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.GeneralResponse;
import aber.dcs.uk.shootingCompetitionsBackend.services.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        "classpath:sqlScripts/averageUsersShootingScores/avg_shooting_scores_clean.sql",
        "classpath:sqlScripts/TableUsersCleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
class RegistrationControllerTest extends HttpTest {
    @SpyBean
    private UserRepository userRepository;

    @Autowired
    RegistrationService registrationService;

    @SpyBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mvcHttp = MockMvcBuilders
                .standaloneSetup(new RegistrationController(registrationService))
                .setControllerAdvice(GlobalExceptionHandler.class).build();
        //any other controller related handles must be added there to run test
    }

    @AfterEach
    void tearDown(){
        clearInvocations(userRepository);
    }

    @Test
    void httpRequestRegisterUserTest_with_correctCredentials_gets_201_response() throws Exception {
        RegisterUserDto registerUserDto = CredentialsHelper.getCorrectUserRegistrationDetails();
        String mockedEncodedPassword = "encoded_password";

        when(passwordEncoder.encode(registerUserDto.getPassword())).thenReturn(mockedEncodedPassword);
        httpBodyParsedData = objectMapper.writeValueAsString(registerUserDto);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/register")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isCreated()).andReturn();

        GeneralResponse actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        GeneralResponse.class);
        assertEquals(actualResponse.getMessage(),
                String.format("User %s registered successfully", registerUserDto.getEmail()));

        UserEntity expectedSavedUser = CredentialsHelper.getDatabaseRegisteredUserEntityWithChangedPassword(
                mockedEncodedPassword);

        verify(userRepository, times(1)).existsByEmail(registerUserDto.getEmail());
        verify(userRepository, times(1)).save(expectedSavedUser);
        verify(passwordEncoder, times(1)).encode(registerUserDto.getPassword());
    }

    @Test
    void httpRequestRegisterUserTest_with_InvalidEmailCredentials_gets_400_response() throws Exception {
        RegisterUserDto registerUserDto = CredentialsHelper.getCorrectUserRegistrationDetails();
        registerUserDto.setEmail("invalidEmail");

        httpBodyParsedData = objectMapper.writeValueAsString(registerUserDto);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/register")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        ErrorResponse actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        ErrorResponse.class);
        assertEquals("Email must be valid", actualResponse.getMessage());


        verify(userRepository, times(0)).findByEmail(registerUserDto.getEmail());
        verify(userRepository, times(0)).save(any());
        verify(passwordEncoder, times(0)).encode(registerUserDto.getPassword());
    }

    @Test
    void httpRequestRegisterUserTest_with_InvalidEmptyCredentials_gets_400_response() throws Exception {
        RegisterUserDto registerUserDto = CredentialsHelper.getCorrectUserRegistrationDetails();
        registerUserDto.setLastname("");
        registerUserDto.setEmail("");

        httpBodyParsedData = objectMapper.writeValueAsString(registerUserDto);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/register")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        ErrorResponse actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        ErrorResponse.class);
        assertTrue(actualResponse.getMessage().contains("Email can't be empty"));
        assertTrue(actualResponse.getMessage().contains("Lastname can't be empty"));


        verify(userRepository, times(0)).findByEmail(registerUserDto.getEmail());
        verify(userRepository, times(0)).save(any());
        verify(passwordEncoder, times(0)).encode(registerUserDto.getPassword());
    }

    @Test
    void httpRequestRegisterUserTest_with_MixInvalidCredentials_gets_400_response() throws Exception {
        RegisterUserDto registerUserDto = CredentialsHelper.getCorrectUserRegistrationDetails();
        registerUserDto.setLastname("");
        registerUserDto.setEmail("InvalidEmail");

        httpBodyParsedData = objectMapper.writeValueAsString(registerUserDto);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/register")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        ErrorResponse actualResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        ErrorResponse.class);

        assertTrue(actualResponse.getMessage().contains("Email must be valid"));
        assertTrue(actualResponse.getMessage().contains("Lastname can't be empty"));


        verify(userRepository, times(0)).findByEmail(registerUserDto.getEmail());
        verify(userRepository, times(0)).save(any());
        verify(passwordEncoder, times(0)).encode(registerUserDto.getPassword());
    }
}