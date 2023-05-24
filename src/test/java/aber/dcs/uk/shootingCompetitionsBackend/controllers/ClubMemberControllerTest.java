package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.config.GlobalExceptionHandler;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.AdminClubMemberDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.ClubMembersHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.deselializer.RestResponsePage;
import aber.dcs.uk.shootingCompetitionsBackend.responses.GeneralResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserMemberResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.admin.ClubMemberResponse;
import aber.dcs.uk.shootingCompetitionsBackend.services.ClubMemberService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"classpath:sqlScripts/multipleUsersDBInit.sql",
        "classpath:sqlScripts/clubsAndClubMembers/Clubs.sql",
        "classpath:sqlScripts/clubsAndClubMembers/ClubMembers.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "classpath:sqlScripts/clubsAndClubMembers/CleanClubMembers.sql",
        "classpath:sqlScripts/TableUsersCleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
class ClubMemberControllerTest extends HttpTest {

    @Autowired
    private ClubMemberService clubMemberService;
    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mvcHttp = MockMvcBuilders
                .standaloneSetup(new ClubMemberController(clubMemberService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalExceptionHandler.class).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getListOfClubMembers_withCorrectClubId_returns_pagedUsersData_OK() throws Exception {
        List<UserMemberResponse> expectedClubMembers = new ArrayList<>(List.of(
                new UserMemberResponse(CredentialsHelper.getUserEntityById(2L)),
                new UserMemberResponse(CredentialsHelper.getUserEntityById(3L))
        ));
        TypeReference<RestResponsePage<UserMemberResponse>> typeRef = new TypeReference<>() {};

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/clubs/1/members")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        RestResponsePage<UserMemberResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        List<UserMemberResponse> pageData = httpResponse.toList();
        assertEquals(2, pageData.size());

        assertArrayEquals(expectedClubMembers.toArray(), pageData.toArray());
    }

    @Test
    void getListOfClubMembers_withClubIdNotInDB_returns_emptyPage_OK() throws Exception {
        TypeReference<RestResponsePage<UserMemberResponse>> typeRef = new TypeReference<>() {};

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/clubs/100/members")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        RestResponsePage<UserMemberResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        List<UserMemberResponse> pageData = httpResponse.toList();
        assertEquals(0, pageData.size());
    }

    @Test
    void getListOfClubMembers_withClubIdInDBButAnyClubMembers_returns_empty_OK() throws Exception {
        TypeReference<RestResponsePage<UserMemberResponse>> typeRef = new TypeReference<>() {};

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .get(rootURI + "/clubs/4/members")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        RestResponsePage<UserMemberResponse> httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                typeRef);
        List<UserMemberResponse> pageData = httpResponse.toList();
        assertEquals(0, pageData.size());
    }

    @Test
    @WithMockUser(username = "admin@nano.com", authorities = { "ADMIN" })
    void addNewClubMember_addsUserWhichIsNotClubMember_returnsNewClubMember() throws Exception {
        UserEntity fakeUser = CredentialsHelper.getUserEntityById(4L);
        ClubEntity club = ClubMembersHelper.ClubHelper.getClubById(4L);

        ClubMemberResponse expectedResponse = new ClubMemberResponse(
            club.getId(),
            club.getClubName(),
            new UserMemberResponse(fakeUser));

        AdminClubMemberDto newClubMember = new AdminClubMemberDto(
                fakeUser.getId());
        String json = objectMapper.writeValueAsString(newClubMember);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/clubs/4/members")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(json);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        ClubMemberResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ClubMemberResponse.class);

        assertEquals(expectedResponse, httpResponse);
    }

    @Test
    @WithMockUser(username = "admin@nano.com", authorities = { "ADMIN" })
    void addNewClubMember_TryAddUserWithClubToOtherClub_returnsChangedClubMember() throws Exception {
        //user is assigned to club with id 1 according to db
        //TODO: check DB for this example in real life scenario
        //make sure that table integrity is valid an each row in clubMembers is unique
        UserEntity fakeSpectatorUser = CredentialsHelper.getUserEntityById(2L);
        ClubEntity club = ClubMembersHelper.ClubHelper.getClubById(2L);

        ClubMemberResponse expectedResponse = new ClubMemberResponse(
                club.getId(),
                club.getClubName(),
                new UserMemberResponse(fakeSpectatorUser));

        AdminClubMemberDto newClubMember = new AdminClubMemberDto(
                fakeSpectatorUser.getId());
        String json = objectMapper.writeValueAsString(newClubMember);
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .post(rootURI + "/clubs/2/members")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(json);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        ClubMemberResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ClubMemberResponse.class);

        assertEquals(expectedResponse, httpResponse);
    }

    @Test
    @WithMockUser(username = "admin@nano.com", authorities = { "ADMIN" })
    void changeClubForUser() throws Exception {
        UserEntity fakeUser = CredentialsHelper.getUserEntityById(1L);
        ClubEntity club = ClubMembersHelper.ClubHelper.getClubById(3L);

        ClubMemberResponse expectedResponse = new ClubMemberResponse(
                club.getId(),
                club.getClubName(),
                new UserMemberResponse(fakeUser));
        String json = objectMapper.writeValueAsString(
                ClubMembersHelper.ClubHelper.getClubById(3L));
        RequestBuilder builder =
                MockMvcRequestBuilders
                        .put(rootURI + "/clubs/2/members/1")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(json);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        ClubMemberResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ClubMemberResponse.class);

        assertEquals(expectedResponse, httpResponse);
    }

    @Test
    @WithMockUser(username = "admin@nano.com", authorities = { "ADMIN" })
    void removeUserFromClub() throws Exception {
        ClubEntity club = ClubMembersHelper.ClubHelper.getClubById(2L);
        UserEntity memberEntity = CredentialsHelper.getUserEntityById(1L);
        GeneralResponse expectedResponse = new GeneralResponse(
            String.format("Club member with id %s has been removed from club \"%s\"",
                    memberEntity.getId(), club.getClubName())
        );

        RequestBuilder builder =
                MockMvcRequestBuilders
                        .delete(rootURI + "/clubs/2/members/1")
                        .contentType(APPLICATION_JSON_UTF8);

        MvcResult mvcResult = mvcHttp.perform(builder).andDo(print())
                .andExpect(status().isOk()).andReturn();

        GeneralResponse httpResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                GeneralResponse.class);

        assertEquals(expectedResponse.getMessage(), httpResponse.getMessage());
    }
}