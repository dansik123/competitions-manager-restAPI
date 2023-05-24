package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.responses.*;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.UpdateUserDetailsDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.AdminRoleUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.NewUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubEntity;
import aber.dcs.uk.shootingCompetitionsBackend.responses.admin.AdminRoleUserResponse;
import aber.dcs.uk.shootingCompetitionsBackend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * HTTP endpoint gives back currently logged-in user in the system
     * @return HTTP response with single user data
     */
    @GetMapping("/current")
    public ResponseEntity<UserResponse> getCurrentUser(){
        return new ResponseEntity<>(userService.getCurrentUser(), HttpStatus.OK);
    }

    /**
     * HTTP endpoint allows current user change its details
     * @param changedUser new user details
     * @return HTTP response with user's changed details
     */
    @PutMapping("/current")
    public ResponseEntity<UserDetailsResponse> putExistingUserOwnDetails(
            @Valid @RequestBody UpdateUserDetailsDto changedUser){
        return new ResponseEntity<>(userService.updateCurrentUserDetails(changedUser), HttpStatus.OK);
    }

    /**
     * HTTP endpoint gives back limited list of users
     * @param usersPageable page for users to display (page, size and resultSorting) default size 5
     * @return HTTP response with page of users
     */
    @GetMapping
    public ResponseEntity<Page<AdminRoleUserResponse>> getLimitedListOfUsers(
            @PageableDefault(size = 5) Pageable usersPageable){
        return new ResponseEntity<>(userService.getPageOfUsers(usersPageable), HttpStatus.OK);
    }

    /**
     * HTTP endpoint gives back single user
     * @param id user's id
     * @return single user
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminRoleUserResponse> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(userService.getSingleUser(id), HttpStatus.OK);
    }

    /**
     * HTTP method to create new user with any role
     * @param newUser new user details object
     * @return AdminRoleUserResponse with base details about new created user
     */
    @PostMapping
    public ResponseEntity<AdminRoleUserResponse> createNewUser(@RequestBody NewUserDto newUser){
        return new ResponseEntity<>(userService.addNewUser(newUser), HttpStatus.CREATED);
    }
    /**
     * HTTP endpoint updates user
     * @param changedUser user's new data
     * @param id user's id
     * @return General response info about successful request
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdminRoleUserResponse> putExistingUser(
            @Valid @RequestBody AdminRoleUserDto changedUser, @PathVariable Long id){
        return new ResponseEntity<>(userService.updateUser(id, changedUser), HttpStatus.OK);
    }

    /**
     * HTTP endpoint delete user
     * @param id user's id
     * @return General response info about successful request
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteExistingUser(@PathVariable Long id){
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }

    /**
     * HTTP endpoint gets information about user's club
     * @param id user id
     * @return HTTP response with user club information
     */
    @GetMapping("/{id}/club")
    public ResponseEntity<ClubEntity> getUserClub(@PathVariable Long id){
        return new ResponseEntity<>(userService.getUserClub(id), HttpStatus.OK);
    }

    /**
     * HTTP get controller method to get average scores for user
     * @param id user's id
     * @return HTTP response with details about user's average scores
     */
    @GetMapping("/{id}/average-scores")
    public ResponseEntity<List<ScoreDetailsResponse>> displayUserAverageScores(
            @PathVariable Long id){
        return new ResponseEntity<>(
                userService.getAverageScoreForUsersAllGunTypes(id),
                HttpStatus.OK);
    }

    /**
     * HTTP endpoint gets information about user's average score for GunType
     * @param id user's id
     * @param gunType user's gunType
     * @return HTTP response with user's average-score for given GunType
     */
    @GetMapping("/{id}/average-scores/{gunType}")
    public ResponseEntity<ScoreDetailsResponse> displayUserAverageScoreGunType(
            @PathVariable Long id,
            @PathVariable String gunType){
        return new ResponseEntity<>(
                userService.getAverageScoreForUserWithGunType(id, gunType),
                HttpStatus.OK);
    }

    /**
     * HTTP endpoint gets information about user's leagues associations
     * @param id user id
     * @return HTTP response with all user's leagues associations
     */
    @GetMapping("/{id}/leagues")
    public ResponseEntity<List<UserLeagueResponse>> displayUserLeagues(
            @PathVariable Long id){
        return new ResponseEntity<>(
                userService.getUserLeagues(id),
                HttpStatus.OK);
    }

    /**
     * HTTP endpoint gets information about user's with average gunType score
     * required to add user to participate in league
     * @param gunType user's selected only if they have average score for gun type
     * @return HTTP response with all user's who can participate for gunType league
     */
    @GetMapping("/league-select")
    public ResponseEntity<Page<UserMemberResponse>> displayUsersToGenerateLeagues(
            @PageableDefault(size = 5) Pageable usersPageable,
            @RequestParam("gunType") String gunType){
        return new ResponseEntity<>(
                userService.getUsersPageForLeagueGeneration(gunType, usersPageable),
                HttpStatus.OK);
    }

}
