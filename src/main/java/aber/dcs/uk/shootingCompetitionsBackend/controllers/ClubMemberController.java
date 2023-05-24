package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.AdminClubMemberDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.NewClubDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubEntity;
import aber.dcs.uk.shootingCompetitionsBackend.responses.GeneralResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserMemberResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.admin.ClubMemberResponse;
import aber.dcs.uk.shootingCompetitionsBackend.services.ClubMemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/clubs")
public class ClubMemberController {
    private final ClubMemberService clubMemberService;

    public ClubMemberController(ClubMemberService clubMemberService) {
        this.clubMemberService = clubMemberService;
    }

    /**
     * HTTP get controller method to get list of all clubs
     * @return HTTP response with JSON clubs data
     */
    @GetMapping
    public ResponseEntity<List<ClubEntity>> getClubsList(){
        return new ResponseEntity<>(clubMemberService.getListOfClubs(), HttpStatus.OK);
    }

    /**
     * HTTP post controller method to add new club.
     * @param newClubDto request body object with new Club name
     * @return HTTP response with new club
     */
    @PostMapping
    public ResponseEntity<ClubEntity> addNewClub(@RequestBody NewClubDto newClubDto){
        return new ResponseEntity<>(clubMemberService.addNewClub(newClubDto), HttpStatus.OK);
    }

    /**
     * HTTP put controller method to change name of the existing club
     * @param clubId existing club id
     * @param newClubDto request body object with new Club name
     * @return HTTP response with changed club
     */
    @PutMapping("/{clubId}")
    public ResponseEntity<ClubEntity> addNewClub(@PathVariable Long clubId,
            @RequestBody NewClubDto newClubDto){
        return new ResponseEntity<>(clubMemberService.editClub(clubId, newClubDto), HttpStatus.OK);
    }

    /**
     * HTTP put controller method to deletes existing club unless there are some users
     * which are assigned to club
     * @param clubId existing club id
     * @return HTTP response with message about successful operation
     */
    @DeleteMapping("/{clubId}")
    public ResponseEntity<GeneralResponse> deleteExistingClub(@PathVariable Long clubId){
        return new ResponseEntity<>(clubMemberService.deleteClub(clubId), HttpStatus.OK);
    }

    /**
     * HTTP get controller method to get limited list members of club
     * @param clubId long club id
     * @param membersPageable object contains page number, page size and page order
     * @return HTTP response with JSON data in the body
     */
    @GetMapping("/{clubId}/members")
    public ResponseEntity<Page<UserMemberResponse>> getListOfClubMembers(
            @PathVariable Long clubId,
            @PageableDefault(size = 5) Pageable membersPageable){

        return new ResponseEntity<>(
                clubMemberService.getAllMembersOfClub(clubId, membersPageable),
                HttpStatus.OK);
    }

    /**
     * HTTP post controller method to add new member to club. If member
     * was assigned to club previously then it will change user's club
     * @param clubId long club id
     * @param clubMemberDto object contains user data required to assign it for club
     * @return HTTP response with JSON data confirmed that new member has been added to the club
     * Error response indicates that something went wrong
     */
    @PostMapping("/{clubId}/members")
    public ResponseEntity<ClubMemberResponse> addNewClubMember(
            @PathVariable Long clubId,
            @Valid @RequestBody AdminClubMemberDto clubMemberDto){

        return new ResponseEntity<>(
                clubMemberService.addNewClubMember(clubId, clubMemberDto),
                HttpStatus.OK);
    }


    /**
     * HTTP put method changes club to which is currently user is assigned to.
     * @param clubId current user's club id
     * @param userId current user's id
     * @param newClub new club object with id and clubName
     * @return HTTP response with JSON data confirmed that new member has been added to the club
     * Error response indicates that something went wrong
     */
    @PutMapping("/{clubId}/members/{userId}")
    public ResponseEntity<ClubMemberResponse> changeClubForUser(
            @PathVariable Long clubId,
            @PathVariable Long userId,
            @RequestBody ClubEntity newClub){

        return new ResponseEntity<>(
                clubMemberService.changeMemberClub(clubId, userId, newClub),
                HttpStatus.OK);
    }

    /**
     * HTTP delete method, it deletes user association with the club
     * @param clubId current user's club id
     * @param userId current user's id
     * @return regular response with information that delete operation
     * was successful or error response if there is any problem
     */
    @DeleteMapping("/{clubId}/members/{userId}")
    public ResponseEntity<GeneralResponse> removeUserFromClub(
            @PathVariable Long clubId,
            @PathVariable Long userId){
        return new ResponseEntity<>(
                clubMemberService.deleteClubMember(clubId, userId),
                HttpStatus.OK
        );
    }
}
