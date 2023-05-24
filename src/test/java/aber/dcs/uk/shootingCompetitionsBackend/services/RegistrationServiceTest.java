package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.RegisterUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.RegistrationException;
import aber.dcs.uk.shootingCompetitionsBackend.helpers.CredentialsHelper;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.AverageShootingScoreRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.GeneralResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Sql(scripts = {
        "classpath:sqlScripts/averageUsersShootingScores/avg_shooting_scores_clean.sql",
        "classpath:sqlScripts/TableUsersCleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
class RegistrationServiceTest {
    private RegistrationService registrationService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @SpyBean
    private UserRepository userRepository;

    @SpyBean
    private AverageShootingScoreRepository averageShootingScoreRepository;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationService(passwordEncoder, userRepository, averageShootingScoreRepository);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(userRepository);
        clearInvocations(passwordEncoder);
    }

    @Test
    void signUpUser_WithCorrectRegisterCredentials_CreatesNewUserInDatabase() {
        RegisterUserDto registerUser = CredentialsHelper.getCorrectUserRegistrationDetails();
        when(
            passwordEncoder.encode(
                    CredentialsHelper.getCorrectUserRegistrationDetails().getPassword())
        ).thenReturn(CredentialsHelper.getDatabaseRegisteredUserEntity().getPassword());
        GeneralResponse responseResult = registrationService.signUpUser(registerUser);
        String expectedMessage = "User " + registerUser.getEmail() + " registered successfully";

        assertEquals(expectedMessage, responseResult.getMessage());
        Optional<UserEntity> dbUser =
                userRepository.findByEmail(
                        CredentialsHelper.getCorrectUserRegistrationDetails().getEmail());
        assertTrue(dbUser.isPresent());
        //id might not always be 1 or 2 therefore I am not comparing it
        assertEquals(dbUser.get().getEmail(), CredentialsHelper.getDatabaseRegisteredUserEntity().getEmail());
        assertEquals(dbUser.get().getPassword(), CredentialsHelper.getDatabaseRegisteredUserEntity().getPassword());
        assertEquals(dbUser.get().getFirstname(), CredentialsHelper.getDatabaseRegisteredUserEntity().getFirstname());
        assertEquals(dbUser.get().getLastname(), CredentialsHelper.getDatabaseRegisteredUserEntity().getLastname());
        assertEquals(dbUser.get().isEnabled(), CredentialsHelper.getDatabaseRegisteredUserEntity().isEnabled());
        for(GrantedAuthority authority: CredentialsHelper.getDatabaseRegisteredUserEntity().getAuthorities()){
            assertTrue(dbUser.get().getAuthorities().contains(authority));
        }
    }

    @Test
    void signUpUser_WithCorrectRegisterCredentialsButUserAlreadyExistsWithThisEmail_throwsException() {
        RegisterUserDto registerUser = CredentialsHelper.getCorrectUserRegistrationDetails();
        when(
                passwordEncoder.encode(
                        CredentialsHelper.getCorrectUserRegistrationDetails().getPassword())
        ).thenReturn(CredentialsHelper.getDatabaseRegisteredUserEntity().getPassword());

        registrationService.signUpUser(registerUser);

        String expectedMessage = "User with email fakeEmail@nano.com already exists";
        assertThrows(RegistrationException.class,
                () ->registrationService.signUpUser(registerUser),
                expectedMessage);

        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(any());
    }
}