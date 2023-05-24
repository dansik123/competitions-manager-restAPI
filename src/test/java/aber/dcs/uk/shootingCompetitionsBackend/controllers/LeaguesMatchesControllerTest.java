package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.config.GlobalExceptionHandler;
import aber.dcs.uk.shootingCompetitionsBackend.config.MediaConfig;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.ShootingResultDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.MatchDateDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.MatchSlotPointsEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingPauseSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.LeagueMatchesHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.MatchSlotPointsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.ShootingPauseSlotsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockAdminAuth;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockSpectatorAuth;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations.UseMockUserOneAuth;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.*;
import aber.dcs.uk.shootingCompetitionsBackend.responses.*;
import aber.dcs.uk.shootingCompetitionsBackend.services.LeaguesMatchesService;
import aber.dcs.uk.shootingCompetitionsBackend.services.ScoreCardsImagesService;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        "classpath:sqlScripts/multipleUsersDBInit.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:sqlScripts/matches/matchesTableCleanup.sql",
        "classpath:sqlScripts/matches/leaguesAndCompetitorsCleanup.sql",
        "classpath:sqlScripts/TableUsersCleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(MERGE)
@RunWith(SpringRunner.class)
@SpringBootTest
class LeaguesMatchesControllerTest extends HttpTest {
    @Autowired
    private LeaguesMatchesService leaguesMatchesService;

    @Autowired
    private MediaConfig mediaConfig;
    @SpyBean
    private LeagueRepository leagueRepository;
    @SpyBean
    private ShootingSlotRepository shootingSlotRepository;

    @SpyBean
    private MatchSlotPointsRepository matchSlotPointsRepository;

    @SpyBean
    private ShootingPauseSlotRepository shootingPauseSlotRepository;
    @SpyBean
    private LeagueCompetitorsRepository leagueCompetitorsRepository;

    @SpyBean
    private ScoreCardsImagesService scoreCardsImagesService;

    @Captor
    ArgumentCaptor<LeagueEntity> updatedLeagueCaptor;
    @Captor
    ArgumentCaptor<List<ShootingSlotEntity>> generatedLeagueMatchSlot;
    @Captor
    ArgumentCaptor<List<ShootingPauseSlotEntity>> generatedLeaguePauseSlot;
    @Captor
    ArgumentCaptor<List<MatchSlotPointsEntity>> generatedLeagueAllMatchesSlotsPoints;

    @Autowired
    private WebApplicationContext webApplicationContext;
    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mvcHttp = MockMvcBuilders
                .standaloneSetup(new LeaguesMatchesController(leaguesMatchesService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalExceptionHandler.class).build();
    }

    @AfterEach
    void tearDown(){
        clearInvocations(shootingSlotRepository);
        clearInvocations(leagueRepository);
        clearInvocations(shootingPauseSlotRepository);
        clearInvocations(matchSlotPointsRepository);
    }

    @Test
    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingPauseSlots.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllLeagueMatchesWith3CompetitorsInLeague_asAnyUser_Response200() throws Exception {
        TypeReference<List<SingleRoundMatchesResponse>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues/1/matches")
                        .contentType(APPLICATION_JSON_UTF8);
        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();
        List<SingleRoundMatchesResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        List<SingleRoundMatchesResponse> expectedResponse =
                LeagueMatchesHelper.threeCompetitorsMatchesManual();

        assertEquals(expectedResponse, httpResponse);
    }

    @Test
    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesFourCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesFourCompetitorsShootingSlots.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllLeagueMatchesWith4CompetitorsInLeague_asAnyUser_Response200() throws Exception {
        TypeReference<List<SingleRoundMatchesResponse>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues/1/matches")
                        .contentType(APPLICATION_JSON_UTF8);
        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();
        List<SingleRoundMatchesResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        List<SingleRoundMatchesResponse> expectedResponse =
                LeagueMatchesHelper.fourCompetitorsMatchesManual();

        assertEquals(expectedResponse, httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void getSingleMatch_whereMatchHave2Competitors_Response200() throws Exception{
        long matchId = 2L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/matches/" + matchId)
                        .contentType(APPLICATION_JSON_UTF8);
        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();
        SingleMatchResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SingleMatchResponse.class);
        SingleMatchResponse expectedResponse = new SingleMatchResponse(
                LeagueMatchesHelper.getSingleMatchFromThreeCompetitorsMatches(2L));

        assertEquals(expectedResponse, httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void getSingleMatch_forNonExistingId_Response404() throws Exception{
        long matchId = 7L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/matches/" + matchId)
                        .contentType(APPLICATION_JSON_UTF8);
        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isNotFound()).andReturn();
        ErrorResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);
        String expectedResponse = "Match with id 7 does not exist";
        assertEquals(expectedResponse, httpResponse.getMessage());
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockUserOneAuth
    void getListOfMatchesAssignedToUser_AsUserWithId3TryToSeeTheirOwnMatches_Response200() throws Exception {
        TypeReference<List<SingleMatchResponse>> typeRef = new TypeReference<>() {};
        long leagueId = 1L;
        long userId = 3L;
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/matches")
                        .param("leagueId", String.valueOf(leagueId))
                        .param("userId", String.valueOf(userId))
                        .contentType(APPLICATION_JSON_UTF8);
        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();
        List<SingleMatchResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        List<SingleMatchResponse> expectedResponse =
                LeagueMatchesHelper.getSlotsWith2CompetitorsByLeagueAndUserIdThreeLeagueCompetitors(leagueId, userId)
                        .stream()
                        .map(SingleMatchResponse::new)
                        .collect(Collectors.toList());

        assertEquals(expectedResponse, httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @UseMockAdminAuth
    @Test
    void updateDateForMatch_asAdminUserAddNewDateToSlot_Response200() throws Exception {
        MatchDateDto matchDateDto = new MatchDateDto(LocalDate.of(2019,6,5));
        httpBodyParsedData = objectMapper.writeValueAsString(matchDateDto);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .patch(rootURI + "/matches/2/date")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);
        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();
        SingleMatchResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SingleMatchResponse.class);
        SingleMatchResponse expectedResponse =
                new SingleMatchResponse(
                        LeagueMatchesHelper.getSingleMatchFromThreeCompetitorsMatches(2L)
                );
        expectedResponse.setMatchDate(LocalDate.parse("2019-06-05"));

        assertEquals(expectedResponse, httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @UseMockAdminAuth
    @Test
    void updateDateForMatch_asAdminUserAddNewDateToNonExistingMatchSlot_Response404() throws Exception {
        MatchDateDto matchDateDto = new MatchDateDto(LocalDate.of(2019,6,5));
        httpBodyParsedData = objectMapper.writeValueAsString(matchDateDto);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .patch(rootURI + "/matches/7/date")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);
        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isNotFound()).andReturn();
        ErrorResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);
        String expectedResponse = "Match with id 7 does not exists";
        assertEquals(expectedResponse, httpResponse.getMessage());
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/threeCompetitorLeagueNotGenerated.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockAdminAuth
    void generateMatchesForLeague_forOddNumberOfCompetitorsInLeagueAsAdmin_shouldGenerateSlotsWithNullCompetitor()
            throws Exception {
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues/1/matches/generate")
                        .contentType(APPLICATION_JSON_UTF8);

        mvcHttp.perform(builder).andDo(print()).andExpect(status().isCreated());

        List<ShootingSlotEntity> expectedMatchSlots = LeagueMatchesHelper.threeCompetitorsMatchesSlotsList();
        //capture saved entities to database
        verify(shootingSlotRepository).saveAll(generatedLeagueMatchSlot.capture());
        //compare their sizes
        assertEquals(expectedMatchSlots.size(), generatedLeagueMatchSlot.getValue().size());

        //check individual entities
        for(int i=0; i < expectedMatchSlots.size(); i++){
            customEqualsShootingSlots(expectedMatchSlots.get(i), generatedLeagueMatchSlot.getValue().get(i));
        }

        //The same procedure for pause slots because they were added during generation due to od number of competitors
        List<ShootingPauseSlotEntity> expectedPauseSlots = ShootingPauseSlotsHelper.threeCompetitorsMatchesPauseSlotsList();

        verify(shootingPauseSlotRepository).saveAll(generatedLeaguePauseSlot.capture());

        assertEquals(expectedMatchSlots.size(), generatedLeaguePauseSlot.getValue().size());

        //check individual entities
        customEqualsShootingPauseSlots(expectedPauseSlots, generatedLeaguePauseSlot.getValue());

        //verify number of generated league slot points entries(should be 2 per each slot)
        verify(matchSlotPointsRepository, times(1)).saveAll(
                generatedLeagueAllMatchesSlotsPoints.capture());
        assertEquals(6, generatedLeagueAllMatchesSlotsPoints.getValue().size());

    }

    private void customEqualsShootingPauseSlots(List<ShootingPauseSlotEntity> expected,
                                                List<ShootingPauseSlotEntity> actual) {
        for(int i=0; i < actual.size(); i++) {
            customEqualsShootingPauseSlots(expected.get(i), actual.get(i));
        }
    }
    private void customEqualsShootingPauseSlots(ShootingPauseSlotEntity slot1, ShootingPauseSlotEntity slot2) {
        assertEquals(slot1.getMatchRoundNumber(), slot2.getMatchRoundNumber());
        assertEquals(slot1.getLeague().getId(), slot2.getLeague().getId());
        assertEquals(slot1.getCompetitor().getId(), slot2.getCompetitor().getId());
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/fourCompetitorLeagueNotGenerated.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockAdminAuth
    void generateMatchesForLeague_forEvenNumberOfCompetitorsInLeagueAsAdmin_shouldGenerateSlotsWithNullCompetitor()
            throws Exception {
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues/1/matches/generate")
                        .contentType(APPLICATION_JSON_UTF8);

        mvcHttp.perform(builder).andDo(print()).andExpect(status().isCreated());

        List<ShootingSlotEntity> expectedMatchSlots = LeagueMatchesHelper.fourCompetitorsMatchesSlotsList();
        //capture saved entities to database
        verify(shootingSlotRepository).saveAll(generatedLeagueMatchSlot.capture());
        //compare their sizes
        assertEquals(expectedMatchSlots.size(), generatedLeagueMatchSlot.getValue().size());

        //check individual entities
        for(int i=0; i < expectedMatchSlots.size(); i++){
            customEqualsShootingSlots(expectedMatchSlots.get(i), generatedLeagueMatchSlot.getValue().get(i));
        }

        //finally check that for event number of competitors there was not any pause slots saved in db
        verify(shootingPauseSlotRepository, times(0)).saveAll(any());

        //verify number of generated league slot points entries(should be 2 per each slot)
        verify(matchSlotPointsRepository, times(1)).saveAll(
                generatedLeagueAllMatchesSlotsPoints.capture());
        assertEquals(12, generatedLeagueAllMatchesSlotsPoints.getValue().size());
    }

    public void customEqualsShootingSlots(ShootingSlotEntity slot1, ShootingSlotEntity slot2){
        //assertEquals(slot1.getId(), slot2.getId());
        assertEquals(slot1.getMatchRoundNumber(), slot2.getMatchRoundNumber());
        assertEquals(slot1.getLeague().getId(), slot2.getLeague().getId());
        assertEquals(slot1.getCompetitor1().getId(), slot2.getCompetitor1().getId());
        assertEquals(slot1.getCompetitor2().getId(), slot2.getCompetitor2().getId());
        assertSame(slot1.getSlotDate(), slot2.getSlotDate());
    }


    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesFourCompetitorsLeague.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockAdminAuth
    void deleteLeagueMatches_asAdminUser_200ResponseAndLeagueMatchesResponseHaveEmptyArray() throws Exception {
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .delete(rootURI + "/leagues/1/matches")
                        .contentType(APPLICATION_JSON_UTF8);

        mvcHttp.perform(builder).andDo(print()).andExpect(status().isAccepted());

        TypeReference<List<SingleRoundMatchesResponse>> typeRef = new TypeReference<>() {};

        builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues/1/matches")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();

        List<SingleRoundMatchesResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);

        assertTrue(httpResponse.isEmpty());
    }


    @Sql(scripts = {
            "classpath:sqlScripts/matches/threeCompetitorLeagueNotGenerated.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockAdminAuth
    void deleteUserMatchesForId3_forOddNumberOfCompetitors_asAdminUser_200ResponseAndLeagueMatchesResponseShouldMatch() throws Exception{
        //generate matches
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues/1/matches/generate")
                        .contentType(APPLICATION_JSON_UTF8);

        mvcHttp.perform(builder).andDo(print()).andExpect(status().isCreated());

        //remove matches related to user with id 3
        builder =
                MockMvcRequestBuilders
                        .delete(rootURI + "/matches")
                        .param("leagueId", "1")
                        .param("userId", "3")
                        .contentType(APPLICATION_JSON_UTF8);

        mvcHttp.perform(builder).andDo(print()).andExpect(status().isAccepted());

        verify(shootingSlotRepository, times(2)).deleteAll(any());
        verify(shootingPauseSlotRepository, times(2)).saveAll(generatedLeaguePauseSlot.capture());
        assertEquals(2, generatedLeaguePauseSlot.getValue().size());
        List<ShootingPauseSlotEntity> shootingPauseSlotEntities = new ArrayList<>(List.of(
                new ShootingPauseSlotEntity(null, new LeagueEntity(1L),
                        CredentialsHelper.getUserEntityById(4L), 1, 2),
                new ShootingPauseSlotEntity(null, new LeagueEntity(1L),
                        CredentialsHelper.getUserEntityById(5L), 1, 3)
        ));

        customEqualsShootingPauseSlots(shootingPauseSlotEntities, generatedLeaguePauseSlot.getValue());

    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/fourCompetitorLeagueNotGenerated.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockAdminAuth
    void deleteUserMatchesForId4_forEvenNumberOfCompetitors_asAdminUser_200ResponseAndLeagueMatchesResponseShouldMatch() throws Exception{
        //generate matches
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/leagues/1/matches/generate")
                        .contentType(APPLICATION_JSON_UTF8);

        mvcHttp.perform(builder).andDo(print()).andExpect(status().isCreated());

        //remove matches related to user with id 4
        builder =
                MockMvcRequestBuilders
                        .delete(rootURI + "/matches")
                        .param("leagueId", "1")
                        .param("userId", "4")
                        .contentType(APPLICATION_JSON_UTF8);

        mvcHttp.perform(builder).andDo(print()).andExpect(status().isAccepted());

        verify(shootingSlotRepository, times(2)).deleteAll(any());
        verify(shootingPauseSlotRepository, times(1)).saveAll(generatedLeaguePauseSlot.capture());
        assertEquals(3, generatedLeaguePauseSlot.getValue().size());
        List<ShootingPauseSlotEntity> shootingPauseSlotEntities = new ArrayList<>(List.of(
                new ShootingPauseSlotEntity(null, new LeagueEntity(1L),
                        CredentialsHelper.getUserEntityById(6L), 1, 3),
                new ShootingPauseSlotEntity(null, new LeagueEntity(1L),
                        CredentialsHelper.getUserEntityById(5L), 1, 1),
                new ShootingPauseSlotEntity(null, new LeagueEntity(1L),
                        CredentialsHelper.getUserEntityById(3L), 1, 2)

        ));

        customEqualsShootingPauseSlots(shootingPauseSlotEntities, generatedLeaguePauseSlot.getValue());
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesFourCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesFourCompetitorsShootingSlots.sql",
            "classpath:sqlScripts/matches/updateSingleSlotDate.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockAdminAuth
    void changeLeagueMatchResult_asAdminUser_200Response() throws Exception {
        ShootingResultDto resultBody = new ShootingResultDto(35, 54);
        httpBodyParsedData = objectMapper.writeValueAsString(resultBody);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .patch(rootURI + "/matches/2/result")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).andExpect(status().isOk()).andReturn();


        SingleMatchResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SingleMatchResponse.class);

        assertEquals("35-54", httpResponse.getSlotMatchResult());
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesFourCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesFourCompetitorsShootingSlots.sql",
            "classpath:sqlScripts/matches/updateSingleSlotDate.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockSpectatorAuth
    void changeLeagueMatchResult_asSpectatorUser_200Response() throws Exception {
        //check match have unmarked result
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/matches/2")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).andExpect(status().isOk()).andReturn();


        SingleMatchResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SingleMatchResponse.class);

        assertEquals("unmarked", httpResponse.getSlotMatchResult());

        //update shooting results for match
        ShootingResultDto resultBody = new ShootingResultDto(32, 45);
        httpBodyParsedData = objectMapper.writeValueAsString(resultBody);
        builder =
                MockMvcRequestBuilders
                        .patch(rootURI + "/matches/2/result")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        mvcResult = mvcHttp.perform(builder).andDo(print()).andExpect(status().isOk()).andReturn();


        httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SingleMatchResponse.class);

        assertEquals("32-45", httpResponse.getSlotMatchResult());
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesFourCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesFourCompetitorsShootingSlots.sql",
            "classpath:sqlScripts/matches/updateSingleSlotDate.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockSpectatorAuth
    void changeLeagueMatchResult_asSpectatorOrAdminUserWhereSlotDidNotHaveDate_409Response() throws Exception {
        //check match have unmarked result
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/matches/3")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).andExpect(status().isOk()).andReturn();


        SingleMatchResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SingleMatchResponse.class);

        assertEquals("unmarked", httpResponse.getSlotMatchResult());
        assertNull(httpResponse.getMatchDate());

        //update shooting results for match with failed attempt
        ShootingResultDto resultBody = new ShootingResultDto(32, 45);
        httpBodyParsedData = objectMapper.writeValueAsString(resultBody);
        builder =
                MockMvcRequestBuilders
                        .patch(rootURI + "/matches/3/result")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        mvcResult = mvcHttp.perform(builder).andDo(print()).andExpect(status().isConflict()).andReturn();
        ErrorResponse httpErrorResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);

        String expectedErrorMessage = "Match with id 3 must have date before adding results";
        assertEquals(expectedErrorMessage, httpErrorResponse.getMessage());
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesFourCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesFourCompetitorsShootingSlots.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @UseMockUserOneAuth
    void changeLeagueMatchResult_asPlainUser_401Response() throws Exception {
        ShootingResultDto resultBody = new ShootingResultDto(32, 45);
        httpBodyParsedData = objectMapper.writeValueAsString(resultBody);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .patch(rootURI + "/matches/2/result")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(httpBodyParsedData);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isUnauthorized()).andReturn();
        ErrorResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ErrorResponse.class);

        assertEquals("Access Denied", httpResponse.getMessage());
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesFourCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesFourCompetitorsShootingSlots.sql",
            "classpath:sqlScripts/matchesLeaguePoints/fourCompetitorsMatchesPoints.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void getSummaryForLeague_forFourCompetitorsLeague_200Response() throws Exception {
        TypeReference<List<SummaryPointsRowResponse<MatchRoundSingleSlotPoints>>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues/1/summary")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();
        List<SummaryPointsRowResponse<MatchRoundSingleSlotPoints>> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        assertEquals(MatchSlotPointsHelper.expectedResponseFourCompetitorsMatches(), httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql",
            "classpath:sqlScripts/matchesLeaguePoints/threeCompetitorsMatchesPoints.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void getSummaryForLeague_forThreeCompetitorsLeague_200Response() throws Exception {
        TypeReference<List<SummaryPointsRowResponse<MatchRoundSingleSlotPoints>>> typeRef = new TypeReference<>() {};
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/leagues/1/summary")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();
        List<SummaryPointsRowResponse<MatchRoundSingleSlotPoints>> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        assertEquals(MatchSlotPointsHelper.expectedResponseThreeCompetitorsMatches(), httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql"})
    @UseMockSpectatorAuth
    @Test
    void uploadImageToCompetitor1AsAdminOrSpectator() throws Exception{
        String resourceFileName = "multipart_test_images/competitor1_score_card.jpg";
        String originalFilename = "competitor1_score_card.jpg";
        MockMultipartFile uploadFile = new MockMultipartFile(
                "file",
                originalFilename,
                MediaType.IMAGE_JPEG_VALUE,
                getClass().getClassLoader().getResourceAsStream(resourceFileName)
        );
        //don't store any data anywhere during test
        doReturn("20020304competitor1_score_card.png").when(scoreCardsImagesService).storeMultiPartImage(any());
        MvcResult mvcResult = mvcHttp.perform(
                multipart(rootURI+"/matches/1/competitor1/scorecard").file(uploadFile))
                    .andDo(print()).andExpect(status().isOk()).andReturn();

        SingleMatchResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SingleMatchResponse.class);

        SingleMatchResponse expectedResponse =
                new SingleMatchResponse(
                        LeagueMatchesHelper.getSingleMatchFromThreeCompetitorsMatches(1L));
        String expectedScoreCardFilename = "20020304competitor1_score_card.png";
        expectedResponse.setCompetitor1ScoreCardLink(expectedScoreCardFilename);

        assertEquals(expectedResponse, httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql"})
    @UseMockSpectatorAuth
    @Test
    void uploadImageToCompetitor2AsAdminOrSpectator() throws Exception{
        String resourceFileName = "multipart_test_images/competitor2_score_card.png";
        String originalFilename = "competitor2_score_card.png";
        MockMultipartFile uploadFile = new MockMultipartFile(
                "file",
                originalFilename,
                MediaType.IMAGE_PNG_VALUE,
                getClass().getClassLoader().getResourceAsStream(resourceFileName)
        );
        //don't store any data anywhere during test
        doReturn("20020304competitor2_score_card.png").when(scoreCardsImagesService).storeMultiPartImage(any());
        MvcResult mvcResult = mvcHttp.perform(
                        multipart(rootURI+"/matches/1/competitor2/scorecard").file(uploadFile))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        SingleMatchResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SingleMatchResponse.class);

        SingleMatchResponse expectedResponse =
                new SingleMatchResponse(
                        LeagueMatchesHelper.getSingleMatchFromThreeCompetitorsMatches(1L));
        String expectedScoreCardFilename = "20020304competitor2_score_card.png";
        expectedResponse.setCompetitor2ScoreCardLink(expectedScoreCardFilename);

        assertEquals(expectedResponse, httpResponse);
    }

    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql"})
    @UseMockSpectatorAuth
    @Test
    void uploadImageToAnyCompetitor2AsAdminOrSpectator_withWrongTypeOfFile() throws Exception{
        String originalFilename = "file.txt";
        MockMultipartFile uploadFile = new MockMultipartFile(
                "file",
                originalFilename,
                MediaType.TEXT_PLAIN_VALUE,
                "text file content".getBytes(StandardCharsets.UTF_8)
        );

        mvcHttp.perform(
                        multipart(rootURI+"/matches/1/competitor2/scorecard").file(uploadFile))
                .andDo(print()).andExpect(status().isUnsupportedMediaType());
    }
    @Sql(scripts = {
            "classpath:sqlScripts/matches/generateMatchesThreeCompetitorsLeague.sql",
            "classpath:sqlScripts/matches/generatedMatchesThreeCompetitorsShootingSlots.sql",
            "classpath:sqlScripts/matches/threeCompetitorsUpdateSingleMatchCompetitorsScoreCards.sql"})
    @Test
    void getImageAssingedToMach_asNonLogInUser_Response200() throws Exception{
        byte[] competitor1ScoreCard = new byte[]{1,2,3,4,5,6,7,8};
        byte[] competitor2ScoreCard = new byte[]{8,7,6,5,4,3,2,1};

        doReturn(competitor1ScoreCard).when(scoreCardsImagesService).readStoredScoreCardImage("competitor1_score_card.jpg");
        doReturn(competitor2ScoreCard).when(scoreCardsImagesService).readStoredScoreCardImage("competitor2_score_card.png");

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/matches/1/competitor1/scorecard");

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();

        String httpResponse = mvcResult.getResponse().getContentAsString();

        assertArrayEquals(competitor1ScoreCard, httpResponse.getBytes(StandardCharsets.UTF_8));

        //check competitor 2
        builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/matches/1/competitor2/scorecard");

        mvcResult = mvcHttp.perform(builder).andDo(print()).
                andExpect(status().isOk()).andReturn();

        httpResponse = mvcResult.getResponse().getContentAsString();

        assertArrayEquals(competitor2ScoreCard, httpResponse.getBytes(StandardCharsets.UTF_8));
    }

}