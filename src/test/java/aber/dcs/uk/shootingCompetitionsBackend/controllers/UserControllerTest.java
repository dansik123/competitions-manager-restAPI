package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.config.GlobalExceptionHandler;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.deselializer.RestResponsePage;
import aber.dcs.uk.shootingCompetitionsBackend.responses.*;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.UpdateUserDetailsDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.AdminRoleUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.NewUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.AverageScoresHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockAdminAuth;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockSpectatorAuth;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockUserOneAuth;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockUserTwoAuth;
import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;
import aber.dcs.uk.shootingCompetitionsBackend.models.Role;
import aber.dcs.uk.shootingCompetitionsBackend.responses.admin.AdminRoleUserResponse;
import aber.dcs.uk.shootingCompetitionsBackend.services.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        "classpath:sqlScripts/multipleUsersDBInit.sql",
        "classpath:sqlScripts/averageUsersShootingScores/avg_shooting_scores_insert.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:sqlScripts/averageUsersShootingScores/avg_shooting_scores_clean.sql",
        "classpath:sqlScripts/TableUsersCleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
class UserControllerTest extends HttpTest {
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mvcHttp = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalExceptionHandler.class).build();
    }

    @Test
    @UseMockAdminAuth
    void getLimitedListOfUsers_withAdminUser_ShouldGiveBackOKResponse() throws Exception {
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @UseMockSpectatorAuth
    void getLimitedListOfUsers_withSpectatorUser_ShouldGiveBack403Response() throws Exception {
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isUnauthorized()).andReturn();
        ErrorResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);
        assertEquals("Access Denied", response.getMessage());
    }

    @Test
    @UseMockUserTwoAuth
    void getLimitedListOfUsers_withPlainUser_ShouldGiveBack403Response() throws Exception {
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isUnauthorized()).andReturn();
        ErrorResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);
        assertEquals("Access Denied", response.getMessage());
    }

    @Test
    @UseMockAdminAuth
    void getUserByID_asAdminUser_ShouldGiveBackOkResponse() throws Exception {
        long userId = 2L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users/"+userId)
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();
        AdminRoleUserResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                AdminRoleUserResponse.class);

        AdminRoleUserResponse expectedResponse =
                new AdminRoleUserResponse(CredentialsHelper.getUserEntityById(userId));
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @UseMockSpectatorAuth
    void getUserByID_asNonAdminUser_ShouldGiveBack403Response() throws Exception {
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users/2")
                        .contentType(APPLICATION_JSON_UTF8);

        mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isUnauthorized());

    }


    @Test
    @UseMockAdminAuth
    @Sql(scripts = {"classpath:sqlScripts/TableUsersCleanup.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createNewUser_asAdminUser_shouldAllowToCreateNewUserWithOkResponse() throws Exception {
        /*
         * Somehow Class scope User table cleanup does not remove this new created test person
         * therefore I have to clean up of user table for this method
         */
        String entityUserExistingPassword = CredentialsHelper.getUserEntityById(3L).getPassword();
        NewUserDto newUser = new NewUserDto(
                "Daniel", "Hamilton", "daniel.hamilton@gmail.com",
                true, Role.SPECTATOR, entityUserExistingPassword);
        AdminRoleUserResponse expectedResponse = new AdminRoleUserResponse(newUser.toUserEntity());
        String requestBody = objectMapper.writeValueAsString(newUser);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/users")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestBody);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isCreated()).andReturn();
        AdminRoleUserResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                AdminRoleUserResponse.class);
        assertTrue(new ReflectionEquals(expectedResponse, "id").matches(actualResponse));
    }

    @Test
    @UseMockUserTwoAuth
    void createNewUser_asPlainUser_shouldAllowToCreateNewUserWithOkResponse() throws Exception {
        String entityUserExistingPassword = CredentialsHelper.getUserEntityById(3L).getPassword();
        NewUserDto newUser = new NewUserDto(
                "Daniel", "Hamilton", "daniel.hamilton@gmail.com",
                true, Role.SPECTATOR, entityUserExistingPassword);
        AdminRoleUserResponse expectedResponse = new AdminRoleUserResponse(newUser.toUserEntity());
        String requestBody = objectMapper.writeValueAsString(newUser);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/users")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestBody);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @UseMockSpectatorAuth
    void createNewUser_asSpectatorUser_shouldAllowToCreateNewUserWithOkResponse() throws Exception {
        String entityUserExistingPassword = CredentialsHelper.getUserEntityById(3L).getPassword();
        NewUserDto newUser = new NewUserDto(
                "Daniel", "Hamilton", "daniel.hamilton@gmail.com",
                true, Role.SPECTATOR, entityUserExistingPassword);
        String requestBody = objectMapper.writeValueAsString(newUser);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/users")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestBody);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @UseMockAdminAuth
    void updateUserData_asAdminUser_ShouldGiveBackOkResponse() throws Exception {
        long userId = 2L;
        UserEntity spectatorUser = CredentialsHelper.getUserEntityById(userId);
        AdminRoleUserDto adminRoleUserDto = new AdminRoleUserDto(spectatorUser);
        //change credentials
        String changedFirstname = "John";
        String changedLastname = "Colby";
        adminRoleUserDto.setFirstname(changedFirstname);
        adminRoleUserDto.setLastname(changedLastname);
        String requestBody = objectMapper.writeValueAsString(adminRoleUserDto);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .put(rootURI + "/users/" + userId)
                        .content(requestBody)
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();
        AdminRoleUserResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                AdminRoleUserResponse.class);

        AdminRoleUserResponse expectedResponse = new AdminRoleUserResponse(spectatorUser);
        //overwrite fields expected to change by put method
        expectedResponse.setFirstname(changedFirstname);
        expectedResponse.setLastname(changedLastname);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @UseMockUserOneAuth
    void updateUserData_asUserWhoUpdatesItsOwnDetails_ShouldGiveBackOkResponse() throws Exception {
        UpdateUserDetailsDto currentUserEntity = new UpdateUserDetailsDto(
                CredentialsHelper.getUserEntityById(3L));
        //change credentials
        String changedFirstname = "John";
        String changedLastname = "Colby";
        currentUserEntity.setFirstname(changedFirstname);
        currentUserEntity.setLastname(changedLastname);
        String requestBody = objectMapper.writeValueAsString(currentUserEntity);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .put(rootURI + "/users/current")
                        .content(requestBody)
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();
        UserDetailsResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserDetailsResponse.class);
        UserDetailsResponse expectedResponse = new UserDetailsResponse(
                CredentialsHelper.getUserEntityById(3L));
        //overwrite fields expected to change by put method
        expectedResponse.setFirstname(changedFirstname);
        expectedResponse.setLastname(changedLastname);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @UseMockUserOneAuth
    void updateUserData_asUserWhoUpdatesOtherUserDetails_ShouldGiveBack403Response() throws Exception {
        long usersId = 4L;
        AdminRoleUserDto adminRoleUserDto =
                new AdminRoleUserDto(CredentialsHelper.getUserEntityById(3L));
        //change credentials
        String changedFirstname = "John";
        String changedLastname = "Colby";
        adminRoleUserDto.setFirstname(changedFirstname);
        adminRoleUserDto.setLastname(changedLastname);
        String requestBody = objectMapper.writeValueAsString(adminRoleUserDto);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .put(rootURI + "/users/" + usersId)
                        .content(requestBody)
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isUnauthorized()).andReturn();
        ErrorResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);
        assertEquals("Access Denied", response.getMessage());
    }

    @Test
    @UseMockUserOneAuth
    void deleteUser_asPlainUser_ShouldGiveBack403Response() throws Exception {
        long usersId = 4L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .delete(rootURI + "/users/" + usersId)
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isUnauthorized()).andReturn();
        ErrorResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);
        assertEquals("Access Denied", response.getMessage());
    }

    @Test
    @UseMockAdminAuth
    //TODO: FLAKY TEST WORKS ONLY EXECUTED ALONE
    void deleteUser_asAdmin_ShouldGiveBackOKResponse() throws Exception {
        long usersId = 2L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .delete(rootURI + "/users/" + usersId)
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();
        GeneralResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                GeneralResponse.class);
        assertEquals("User with id 2 has been deleted", response.getMessage());
    }

    @Test
    @UseMockAdminAuth
    void tryDeleteNonExistingUser_asAdmin_ShouldGiveBack403Response() throws Exception {
        long usersId = 10L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .delete(rootURI + "/users/" + usersId)
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isNotFound()).andReturn();
        ErrorResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);
        assertEquals("User with id 10 does not exists", response.getMessage());
    }

    //USER AVERAGE SCORE TESTS
    @Test
    @UseMockAdminAuth
    void displayUserAverageScores_forAdminUserWithOnlySingleGunType_returnsOkResponse() throws Exception {
        UserAverageScoreResponse expectedResponse = AverageScoresHelper.getAverageScoreResponseByUserId(3L);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users/3/average-scores/rifle")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        ScoreDetailsResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ScoreDetailsResponse.class);

        ScoreDetailsResponse singleScoreDetail = expectedResponse.getScoresDetails().
                stream().
                filter(scoreDetail -> scoreDetail.getGunType().equals(GunType.RIFLE.name())).
                findFirst().orElse(null);

        Assertions.assertEquals(singleScoreDetail, httpResponse);
    }

    @Test
    @UseMockAdminAuth
    void displayUserAverageScores_forAdminUserWithAllAvgScores_returnsOkResponse() throws Exception {
        List<ScoreDetailsResponse> expectedResponse =
                AverageScoresHelper.getAverageScoreResponseByUserId(3L).getScoresDetails();
        TypeReference<List<ScoreDetailsResponse>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users/3/average-scores")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<ScoreDetailsResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        Assertions.assertEquals(expectedResponse, httpResponse);
    }

    @Test
    @UseMockAdminAuth
    void displayUserAverageScores_forAdminUserWithAllAvgScoresAndUserWhereUserHasOnlyOneGunTypeInDb_returnsOkResponse()
            throws Exception {
        List<ScoreDetailsResponse> expectedResponse =
                AverageScoresHelper.getAverageScoreResponseByUserId(6L).getScoresDetails();
        TypeReference<List<ScoreDetailsResponse>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users/6/average-scores")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<ScoreDetailsResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        Assertions.assertEquals(expectedResponse, httpResponse);
    }

    @Test
    @UseMockUserOneAuth
    void displayUserAverageScores_userGetsItsOwnAllAvgScores_returnsOkResponse()
            throws Exception {
        List<ScoreDetailsResponse> expectedResponse =
                AverageScoresHelper.getAverageScoreResponseByUserId(3L).getScoresDetails();
        TypeReference<List<ScoreDetailsResponse>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users/3/average-scores")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<ScoreDetailsResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        Assertions.assertEquals(expectedResponse, httpResponse);
    }

    @Test
    @UseMockUserOneAuth
    void displayUserAverageScores_userGetsItsOwnRifleAvgScore_returnsOkResponse() throws Exception {
        UserAverageScoreResponse expectedResponse = AverageScoresHelper.getAverageScoreResponseByUserId(3L);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users/3/average-scores/rifle")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        ScoreDetailsResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ScoreDetailsResponse.class);

        ScoreDetailsResponse singleScoreDetail = expectedResponse.getScoresDetails().
                stream().
                filter(scoreDetail -> scoreDetail.getGunType().equals(GunType.RIFLE.name())).
                findFirst().orElse(null);

        Assertions.assertEquals(singleScoreDetail, httpResponse);
    }

    @Test
    @UseMockAdminAuth
    void displayUsersToGenerateLeagues_asAdminUser_returns200Response() throws Exception {
        TypeReference<RestResponsePage<UserMemberResponse>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users/league-select")
                        .param("gunType", "pistol")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        RestResponsePage<UserMemberResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        List<UserMemberResponse> selectedUsers = new ArrayList<>(List.of(
                new UserMemberResponse(CredentialsHelper.getUserEntityById(6L)),
                new UserMemberResponse(CredentialsHelper.getUserEntityById(3L)),
                new UserMemberResponse(CredentialsHelper.getUserEntityById(5L)),
                new UserMemberResponse(CredentialsHelper.getUserEntityById(4L))
        ));

        Assertions.assertEquals(selectedUsers, httpResponse.toList());
    }

    @Test
    @UseMockAdminAuth
    void displayUsersToGenerateLeagues_asAdminUser_returns200ResponseWithEmptyPage() throws Exception {
        TypeReference<RestResponsePage<UserMemberResponse>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/users/league-select")
                        .param("gunType", "unknown")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        RestResponsePage<UserMemberResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        Assertions.assertTrue(httpResponse.toList().isEmpty());
    }
}