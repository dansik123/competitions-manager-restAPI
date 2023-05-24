package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.AverageShootingScoreRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.ClubMemberRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.LeagueCompetitorsRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.clearInvocations;

@Sql(scripts = {"classpath:sqlScripts/SingleRegisteredUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:sqlScripts/TableUsersCleanup.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {

    @SpyBean
    UserRepository userRepository;

    @Autowired
    ClubMemberRepository clubMemberRepository;

    @Autowired
    AverageShootingScoreRepository averageShootingScoreRepository;

    @Autowired
    LeagueCompetitorsRepository leagueCompetitorsRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, clubMemberRepository,
                averageShootingScoreRepository, leagueCompetitorsRepository, passwordEncoder);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(userRepository);
    }

    @Test
    @WithMockUser(username = "fakeEmail@nano.com", roles = { "USER" })
    void getCurrentUser_WithAuthorizedUserInContextAndUserSavedInDatabase_givesBackCorrectUserResponse() {
        UserEntity databaseUser = CredentialsHelper.getDatabaseRegisteredUserEntity();

        UserResponse result = userService.getCurrentUser();
        assertEquals(databaseUser.getEmail(), result.getEmail());
        assertEquals(databaseUser.getLastname(), result.getLastname());
        assertEquals(databaseUser.getFirstname(), result.getFirstname());
        assertEquals(databaseUser.getId(), result.getId());

        Optional<? extends GrantedAuthority> authority =
                databaseUser.getAuthorities().
                        stream().findFirst();
        assertTrue(authority.isPresent());


        assertEquals(authority.get().getAuthority(), result.getRole());
    }

    @Test
    @WithMockUser(username = "fakeEmail@nano.com", roles = { "USER" })
    @Sql(scripts = {"classpath:sqlScripts/TableUsersCleanup.sql"})
    void getCurrentUser_WithOnlyAuthorizedUserInContext_throwsException() {

        assertThrows(RuntimeException.class,
                () -> userService.getCurrentUser(),
                "Current authenticated user with email fakeEmail@nano.com not found in database");
    }



    @Test
    void getCurrentUser_WithOnlyUserSavedInDatabase_throwsError() {
        assertThrows(RuntimeException.class,
                () -> userService.getCurrentUser(),
                "No user logged in");
    }

    @Test
    void getPageOfUsers() {
    }
}