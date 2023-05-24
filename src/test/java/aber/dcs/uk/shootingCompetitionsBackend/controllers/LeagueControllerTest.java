package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.config.GlobalExceptionHandler;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.SaveLeagueDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.UpdateLeagueDto;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.LeaguesEntityHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.UserLeagueRowHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockAdminAuth;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockSpectatorAuth;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockUserOneAuth;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockUserTwoAuth;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.deselializer.RestResponsePage;
import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.LeagueRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.*;
import aber.dcs.uk.shootingCompetitionsBackend.services.LeagueCompetitorsService;
import aber.dcs.uk.shootingCompetitionsBackend.services.LeagueService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        "classpath:sqlScripts/multipleUsersDBInit.sql",
        "classpath:sqlScripts/averageUsersShootingScores/avg_shooting_scores_insert.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:sqlScripts/leagueCompetitorsCleanup.sql",
        "classpath:sqlScripts/leaguesCleanup.sql",
        "classpath:sqlScripts/averageUsersShootingScores/avg_shooting_scores_clean.sql",
        "classpath:sqlScripts/TableUsersCleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
class LeagueControllerTest extends HttpTest {
    @Autowired
    private LeagueCompetitorsService leagueCompetitorsService;

    @Autowired
    LeagueService leagueService;

    @SpyBean
    LeagueRepository leagueRepository;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mvcHttp = MockMvcBuilders
                .standaloneSetup(new LeagueController(leagueCompetitorsService, leagueService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalExceptionHandler.class).build();
    }

    @AfterEach
    void tearDown() {
        clearInvocations(leagueRepository);
    }

    @Test
    @UseMockAdminAuth
    void generateLeaguesForGunType_AsAdminWhereNumberOfCompetitorsFillInAllSpotsInEachNewLeague_Response200()
            throws Exception {
        Map<String, List<Long>> requestBodyMap = new HashMap<>();
        List<Long> userIdsList = new ArrayList<>(List.of(
                Long.valueOf("3"), Long.valueOf("4")));
        requestBodyMap.put(GunType.RIFLE.name(), userIdsList);
        httpBodyParsedData = objectMapper.writeValueAsString(requestBodyMap);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues/generate")
                        .param("leagueSize", "2")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<LeagueCompetitorRowTableResponse> league1Competitors = new ArrayList<>(List.of(
                UserLeagueRowHelper.getUserLeagueRowResponse(4L, GunType.RIFLE),
                UserLeagueRowHelper.getUserLeagueRowResponse(3L, GunType.RIFLE)
        ));


        List<LeagueGenerateResponse> expectedResponse = new ArrayList<>(List.of(
                new LeagueGenerateResponse("RIFLE_LEAGUE_1", league1Competitors)
        ));

        TypeReference<List<LeagueGenerateResponse>> typeRef = new TypeReference<>() {};
        List<LeagueGenerateResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        assertEquals(expectedResponse, httpResponse);
    }

    @Test
    @UseMockAdminAuth
    void generateLeaguesForGunType_AsAdminWhereNumberOfCompetitorsNotFillInAllSpotsInEachNewLeague_Response200()
            throws Exception {
        Map<String, List<Long>> requestBodyMap = new HashMap<>();
        List<Long> userIdsList = new ArrayList<>(List.of(
                Long.valueOf("3"), Long.valueOf("5"), Long.valueOf("6")));
        requestBodyMap.put(GunType.PISTOL.name(), userIdsList);
        httpBodyParsedData = objectMapper.writeValueAsString(requestBodyMap);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues/generate")
                        .param("leagueSize", "2")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<LeagueCompetitorRowTableResponse> league1Competitors = new ArrayList<>(List.of(
                UserLeagueRowHelper.getUserLeagueRowResponse(3L, GunType.PISTOL),
                UserLeagueRowHelper.getUserLeagueRowResponse(6L, GunType.PISTOL)
        ));

        List<LeagueCompetitorRowTableResponse> league2Competitors = new ArrayList<>(List.of(
                UserLeagueRowHelper.getUserLeagueRowResponse(5L, GunType.PISTOL)
        ));

        List<LeagueGenerateResponse> expectedResponse = new ArrayList<>(List.of(
                new LeagueGenerateResponse("PISTOL_LEAGUE_1", league1Competitors),
                new LeagueGenerateResponse("PISTOL_LEAGUE_2", league2Competitors)
        ));

        TypeReference<List<LeagueGenerateResponse>> typeRef = new TypeReference<>() {};
        List<LeagueGenerateResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        assertEquals(expectedResponse, httpResponse);
    }

    @Test
    @UseMockSpectatorAuth
    void generateLeaguesForGunType_AsSpectatorUser_Response403() throws Exception {
        Map<String, List<Long>> requestBodyMap = new HashMap<>();
        List<Long> userIdsList = new ArrayList<>(List.of(
                Long.valueOf("3"), Long.valueOf("5"), Long.valueOf("6")));
        requestBodyMap.put(GunType.PISTOL.name(), userIdsList);
        httpBodyParsedData = objectMapper.writeValueAsString(requestBodyMap);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues/generate")
                        .param("leagueSize", "2")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @UseMockUserOneAuth
    void generateLeaguesForGunType_AsPlainUser_Response403() throws Exception {
        Map<String, List<Long>> requestBodyMap = new HashMap<>();
        List<Long> userIdsList = new ArrayList<>(List.of(
                Long.valueOf("3"), Long.valueOf("5"), Long.valueOf("6")));
        requestBodyMap.put(GunType.PISTOL.name(), userIdsList);
        httpBodyParsedData = objectMapper.writeValueAsString(requestBodyMap);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues/generate")
                        .param("leagueSize", "2")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @UseMockAdminAuth
    void saveLeagues_AsAdminUser_Response201() throws Exception {
        Map<String, List<Long>> leaguesCompetitors = new HashMap<>();
        leaguesCompetitors.put("PISTOL_LEAGUE_1", new ArrayList<>(List.of(
                Long.valueOf("3"), Long.valueOf("6")
        )));
        leaguesCompetitors.put("PISTOL_LEAGUE_2", new ArrayList<>(List.of(
                Long.valueOf("5"), Long.valueOf("4")
        )));

        SaveLeagueDto newLeaguesGroups = new SaveLeagueDto();
        newLeaguesGroups.setLeagueGunType("PISTOL");
        newLeaguesGroups.setLeagueMaxCompetitors(2);
        newLeaguesGroups.setLeaguesGroups(leaguesCompetitors);
        newLeaguesGroups.setRoundsToPlay(2);

        httpBodyParsedData = objectMapper.writeValueAsString(newLeaguesGroups);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isCreated());

        verify(leagueRepository, times(4)).save(any());
    }

    @Test
    @UseMockAdminAuth
    void saveLeaguesWhereOneLeagueContainsJustOneUser_AsAdminUser_Response400() throws Exception {
        Map<String, List<Long>> leaguesCompetitors = new HashMap<>();
        leaguesCompetitors.put("PISTOL_LEAGUE_1", new ArrayList<>(List.of(
                Long.valueOf("3"), Long.valueOf("6")
        )));
        leaguesCompetitors.put("PISTOL_LEAGUE_2", new ArrayList<>(List.of(
                Long.valueOf("5")
        )));

        SaveLeagueDto newLeaguesGroups = new SaveLeagueDto();
        newLeaguesGroups.setLeagueGunType("PISTOL");
        newLeaguesGroups.setLeagueMaxCompetitors(2);
        newLeaguesGroups.setLeaguesGroups(leaguesCompetitors);
        newLeaguesGroups.setRoundsToPlay(2);

        httpBodyParsedData = objectMapper.writeValueAsString(newLeaguesGroups);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isBadRequest()).andReturn();
        ErrorResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);
        String expectedErrorMessage = "You can't create league with 0 or 1 competitor";
        assertEquals(expectedErrorMessage, httpResponse.getMessage());
    }

    @Sql(scripts = {
            "classpath:sqlScripts/leagues.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sqlScripts/leaguesCleanup.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getLeaguePage_AsAnyUser_Response200() throws Exception {
        TypeReference<RestResponsePage<UserLeagueResponse>> typeRef = new TypeReference<>() {};

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();
        RestResponsePage<UserLeagueResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        List<UserLeagueResponse> responsePageContent = httpResponse.getContent();
        List<UserLeagueResponse> expectedResponseContent = new ArrayList<>(List.of(
            new UserLeagueResponse(1L, "PISTOL_LEAGUE_1", 2, 2, 2, 1, false, "PISTOL"),
            new UserLeagueResponse(2L, "PISTOL_LEAGUE_2", 2, 2, 2, 1, false, "PISTOL"),
            new UserLeagueResponse(3L, "PISTOL_OTHER_LEAGUE_1", 2, 2, 2, 1, false, "PISTOL"),
            new UserLeagueResponse(4L, "PISTOL_OTHER_LEAGUE_2", 2, 2, 2, 1, false, "PISTOL")
        ));
        assertEquals(expectedResponseContent, responsePageContent);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/multipleUsersDBInit.sql",
            "classpath:sqlScripts/averageUsersShootingScores/avg_shooting_scores_insert.sql",
            "classpath:sqlScripts/leagues.sql",
            "classpath:sqlScripts/leaguesCompetitors.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sqlScripts/leagueCompetitorsCleanup.sql",
            "classpath:sqlScripts/leaguesCleanup.sql",
            "classpath:sqlScripts/averageUsersShootingScores/avg_shooting_scores_clean.sql",
            "classpath:sqlScripts/TableUsersCleanup.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getLeagueCompetitors_AsAnyUser_Response200() throws Exception {
        TypeReference<List<LeagueCompetitorRowTableResponse>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues/1/competitors")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();
        List<LeagueCompetitorRowTableResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        List<LeagueCompetitorRowTableResponse> expectedResponseArray =
            new ArrayList<>(List.of(
                UserLeagueRowHelper.getUserLeagueRowResponse(3L, GunType.PISTOL),
                UserLeagueRowHelper.getUserLeagueRowResponse(4L, GunType.PISTOL)
            ));

        assertEquals(expectedResponseArray, httpResponse);
    }

    @Test
    @Sql(scripts = {
            "classpath:sqlScripts/leagues.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sqlScripts/leaguesCleanup.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @UseMockAdminAuth
    void updateLeague_asAdminUser_Response200() throws Exception {
        String newLeagueName = "NEW_PISTOL_LEAGUE_1";
        UpdateLeagueDto updateLeagueDto = new UpdateLeagueDto(newLeagueName);

        httpBodyParsedData = objectMapper.writeValueAsString(updateLeagueDto);

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .put(rootURI + "/leagues/1")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();
        UserLeagueResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserLeagueResponse.class);
        UserLeagueResponse expectedResponse = new UserLeagueResponse(
                1L, newLeagueName, 2, 2, 2, 1, false, GunType.PISTOL.name());

        assertEquals(expectedResponse, httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/multipleUsersDBInit.sql",
            "classpath:sqlScripts/leagues.sql",
            "classpath:sqlScripts/leaguesCompetitors.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sqlScripts/leagueCompetitorsCleanup.sql",
            "classpath:sqlScripts/leaguesCleanup.sql",
            "classpath:sqlScripts/TableUsersCleanup.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @UseMockAdminAuth
    void deleteLeagueAndLeagueCompetitors_asAdminUser_Response200() throws Exception {

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .delete(rootURI + "/leagues/1")
                        .contentType(APPLICATION_JSON_UTF8);

        mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isNoContent());

        builder =
                MockMvcRequestBuilders
                    .get(rootURI + "/leagues/1/competitors")
                    .contentType(APPLICATION_JSON_UTF8);
        mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(scripts = {
            "classpath:sqlScripts/leagues.sql" },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sqlScripts/leaguesCleanup.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getLeagueById_AsAnyUser_Response200() throws Exception{
        long leagueId = 1L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues/"+ leagueId)
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        UserLeagueResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserLeagueResponse.class);
        UserLeagueResponse expectedResponse = new UserLeagueResponse(
                LeaguesEntityHelper.getLeagueEntityById(leagueId));

        assertEquals(expectedResponse, httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/multipleUsersDBInit.sql",
            "classpath:sqlScripts/leagues.sql",
            "classpath:sqlScripts/leaguesCompetitors.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sqlScripts/leagueCompetitorsCleanup.sql",
            "classpath:sqlScripts/leaguesCleanup.sql",
            "classpath:sqlScripts/TableUsersCleanup.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @UseMockUserTwoAuth
    @Test
    void getSelectableLists_forUserId4_200Response() throws Exception {
        TypeReference<List<LeagueSelectResponse>> typeRef = new TypeReference<>() {};
        long userId = 4L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues/selectable")
                        .param("userId", String.valueOf(userId))
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<LeagueSelectResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        List<LeagueSelectResponse> expectedResponse = new ArrayList<>(List.of(
                new LeagueSelectResponse(LeaguesEntityHelper.getLeagueEntityById(1L))
        ));

        assertEquals(expectedResponse, httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/multipleUsersDBInit.sql",
            "classpath:sqlScripts/leagues.sql",
            "classpath:sqlScripts/leaguesCompetitors.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sqlScripts/leagueCompetitorsCleanup.sql",
            "classpath:sqlScripts/leaguesCleanup.sql",
            "classpath:sqlScripts/TableUsersCleanup.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @UseMockSpectatorAuth
    @Test
    void getSelectableLists_forUserId2_200ResponseWithEmptyArray() throws Exception {
        TypeReference<List<LeagueSelectResponse>> typeRef = new TypeReference<>() {};
        long userId = 2L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues/selectable")
                        .param("userId", String.valueOf(userId))
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<LeagueSelectResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        assertTrue(httpResponse.isEmpty());
    }
}