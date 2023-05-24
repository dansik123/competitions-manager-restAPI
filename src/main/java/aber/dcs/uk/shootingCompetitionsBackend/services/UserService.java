package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ClubDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.LeagueDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.ScoreDetailsDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.UserMemberDao;
import aber.dcs.uk.shootingCompetitionsBackend.responses.*;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.UpdateUserDetailsDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.AdminRoleUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.NewUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.CustomHttpException;
import aber.dcs.uk.shootingCompetitionsBackend.models.Role;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.AverageShootingScoreRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.ClubMemberRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.LeagueCompetitorsRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.admin.AdminRoleUserResponse;
import aber.dcs.uk.shootingCompetitionsBackend.security.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final AverageShootingScoreRepository averageShootingScoreRepository;
    private final LeagueCompetitorsRepository leagueCompetitorsRepository;
    private final PasswordEncoder passwordEncoder;

    private final static String USERNAME_NOT_FOUND_MSG =
            "Current authenticated user with email %s not found in database";

    public UserService(UserRepository userRepository,
                       ClubMemberRepository clubMemberRepository,
                       AverageShootingScoreRepository averageShootingScoreRepository,
                       LeagueCompetitorsRepository leagueCompetitorsRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clubMemberRepository = clubMemberRepository;
        this.averageShootingScoreRepository = averageShootingScoreRepository;
        this.leagueCompetitorsRepository = leagueCompetitorsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Method gets current logged in user
     * @return user response data
     */
    public UserResponse getCurrentUser(){
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(
                () -> new RuntimeException("No user logged in")
        );
        UserEntity user = userRepository.findByEmail(currentUserLogin).orElseThrow(
                () -> new RuntimeException(USERNAME_NOT_FOUND_MSG)
        );
        return new UserResponse(user);
    }

    /**
     * ADMIN ACCESS ONLY
     * Method gets single user from database
     * @param id user's id
     * @return Founded user
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public AdminRoleUserResponse getSingleUser(Long id){
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException(USERNAME_NOT_FOUND_MSG));
        return new AdminRoleUserResponse(user);
    }

    /**
     * ADMIN ACCESS ONLY
     * Gives back limited list of users specified by Pageable parameter
     * @param pageable choose the page for users to display (page, size and resultSorting)
     * @return Page of UserResponse without some user details like password
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<AdminRoleUserResponse> getPageOfUsers(Pageable pageable){
        String currentUser = SecurityUtils.getCurrentUserLogin().orElseThrow(
                ()-> new AuthenticationServiceException("Request in not authenticated"));
        return userRepository.findByEmailNot(currentUser, pageable).map(AdminRoleUserResponse::new);
    }

    /**
     * ADMIN ACCESS ONLY
     * Methods adds new user to database
     * @param newUser new user's details
     * @return AdminRoleUserResponse with base details about new created user
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public AdminRoleUserResponse addNewUser(NewUserDto newUser) {
        UserEntity newEntityUser = newUser.toUserEntity();
        if(userRepository.existsByEmail(newUser.getEmail())){
            throw new CustomHttpException(
                    String.format("User with email %s already exists", newUser.getEmail()),
                    HttpStatus.CONFLICT);
        }
        String encodedPassword = passwordEncoder.encode(newEntityUser.getPassword());
        newEntityUser.setPassword(encodedPassword);

        UserEntity savedUser = userRepository.save(newEntityUser);
        return new AdminRoleUserResponse(savedUser);
    }

    /**
     * ADMIN ACCESS ONLY
     * Method updates user
     * @param userId update user id
     * @param updatedUser update user data
     * @return General Response for successful operation
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public AdminRoleUserResponse updateUser(Long userId, AdminRoleUserDto updatedUser) {
        //ADMINS can also update other user's data
        UserEntity toUpdateUser = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException(String.format(USERNAME_NOT_FOUND_MSG, userId)));
        toUpdateUser.setFirstname(updatedUser.getFirstname());
        toUpdateUser.setLastname(updatedUser.getLastname());
        toUpdateUser.setEmail(updatedUser.getEmail());
        toUpdateUser.setEnabled(updatedUser.getEnabled());
        toUpdateUser.setRole(Role.valueOf(updatedUser.getRole().toUpperCase(Locale.ROOT)));

        return new AdminRoleUserResponse(userRepository.save(toUpdateUser));
    }

    public UserDetailsResponse updateCurrentUserDetails(UpdateUserDetailsDto changedUser) {
        String userEmail = SecurityUtils.getCurrentUserLogin().orElseThrow(
                () -> new CustomHttpException("User Authorization error", HttpStatus.UNAUTHORIZED)
        );
        UserEntity dbUser = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new CustomHttpException("User not found", HttpStatus.NOT_FOUND)
        );

        dbUser.setFirstname(changedUser.getFirstname());
        dbUser.setLastname(changedUser.getLastname());
        dbUser.setEmail(changedUser.getEmail());
        UserEntity updatedUser = userRepository.save(dbUser);
        return new UserDetailsResponse(updatedUser);
    }

    /**
     * ADMIN ACCESS ONLY
     * Deletes user
     * @param id user's id to delete
     * @return General Response for successful operation
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public GeneralResponse deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new CustomHttpException(
                    String.format("User with id %s does not exists", id),
                    HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
        return new GeneralResponse(String.format("User with id %d has been deleted", id));
    }

    /**
     * Method get userMember club
     * @param userId users id
     * @return club assigned to member
     */
    public ClubEntity getUserClub(Long userId){
        if(!userRepository.existsById(userId)){
            throw new CustomHttpException(
                    String.format("User with id %s does not exists", userId),
                    HttpStatus.NOT_FOUND);
        }

        ClubDao clubDao = clubMemberRepository.getClubByUserId(userId).orElseThrow(
            () -> new CustomHttpException(
                    String.format("User %s is not a member of any club", userId),
                    HttpStatus.NOT_FOUND)
        );
        return new ClubEntity(clubDao);
    }

    /**
     * Method gets single average shooting score with some user's details
     * @param userId user's id
     * @param gunType type of gun to which avg score was given
     * @return UserAverageScoreResponse with details about user and single average score
     * @throws CustomHttpException if there is a problem to find user or shooting average
     * @throws AccessDeniedException if user which access resource does not have enough privileges
     */
    public ScoreDetailsResponse getAverageScoreForUserWithGunType(Long userId, String gunType)
            throws CustomHttpException, AccessDeniedException{
        UserMemberDao userDetails = userRepository.findByIdSmallLimitedColumns(userId).orElseThrow(
                () -> new CustomHttpException(
                        String.format("User with id %s not found", userId),
                        HttpStatus.NOT_FOUND)
        );

        ScoreDetailsDao scoreDetailsDao = averageShootingScoreRepository.findScoreDetailByUserIdAndGunType(
                userDetails.getUserId(), gunType.toUpperCase(Locale.ROOT)).orElseThrow(
                () -> new CustomHttpException(
                        String.format("Average score for user %s and gun type %s not found", userId, gunType),
                        HttpStatus.NOT_FOUND)
        );
        return new ScoreDetailsResponse(scoreDetailsDao);
    }

    /**
     * Method gets all average shooting scores associated with user + some user's details
     * @param userId user's id
     * @return UserAverageScoreResponse with details about user and multiple average score
     * @throws CustomHttpException if there is a problem to find user
     * @throws AccessDeniedException if user which access resource does not have enough privileges
     */
    public List<ScoreDetailsResponse> getAverageScoreForUsersAllGunTypes(Long userId)
            throws CustomHttpException, AccessDeniedException{

        UserMemberDao userDetails = userRepository.findByIdSmallLimitedColumns(userId).orElseThrow(
                () -> new CustomHttpException(
                        String.format("User with id %s not found", userId),
                        HttpStatus.NOT_FOUND)
        );

        return averageShootingScoreRepository.findAllScoreDetailsByUserId(userDetails.getUserId()).
                        stream().map(ScoreDetailsResponse::new).toList();
    }

    /**
     * Method gets list of leagues assigned to user
     * @param userId user id
     * @return list of leagues assigned to user
     */
    public List<UserLeagueResponse> getUserLeagues(Long userId){
        List<LeagueDao> databaseUserLeagues =
                leagueCompetitorsRepository.getLeaguesAssociatedWithUser(userId);
        return databaseUserLeagues.stream().
                map(UserLeagueResponse::new).
                collect(Collectors.toList());
    }

    /**
     * ADMIN ACCESS ONLY
     * Method gets limit number of users who have average score for given gunType
     * @param gunType gun type like RIFLE, PISTOL
     * @param pageable how many user's page should have and how should be ordered
     * @return Page of users
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<UserMemberResponse> getUsersPageForLeagueGeneration(String gunType, Pageable pageable){
        String upperCaseGunType = gunType.toUpperCase(Locale.ROOT);
        return userRepository.findByUsersWithGunTypeAverageScore(upperCaseGunType, pageable).
                map(UserMemberResponse::new);
    }
}
