package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.SaveLeagueDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.UpdateLeagueDto;
import aber.dcs.uk.shootingCompetitionsBackend.responses.*;
import aber.dcs.uk.shootingCompetitionsBackend.services.LeagueCompetitorsService;
import aber.dcs.uk.shootingCompetitionsBackend.services.LeagueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/leagues")
public class LeagueController {
    private final LeagueCompetitorsService leagueCompetitorsService;
    private final LeagueService leagueService;

    public LeagueController(LeagueCompetitorsService leagueCompetitorsService,
                            LeagueService leagueService) {
        this.leagueCompetitorsService = leagueCompetitorsService;
        this.leagueService = leagueService;
    }

    /**
     * HTTP endpoint gives back limited list of leagues
     * @param leaguePageable page for leagues to display (page, size and resultSorting) default size 5
     * @return HTTP response with single page of all leagues stored in the system
     */
    @GetMapping
    public ResponseEntity<Page<UserLeagueResponse>> getLeaguePage(
            @PageableDefault(size = 5) Pageable leaguePageable){
        return new ResponseEntity<>(
                leagueService.getLeaguesListPage(leaguePageable),
                HttpStatus.OK);
    }

    /**
     * HTTP endpoint gives back list of leagues associated with user
     * @param userId user's id
     * @return HTTP response with league id and name
     */
    @GetMapping("/selectable")
    public ResponseEntity<List<LeagueSelectResponse>> getSelectableLists(
            @RequestParam("userId") Long userId
    ){
        return  new ResponseEntity<>(
                leagueCompetitorsService.getAllUserLeagues(userId),
                HttpStatus.OK);
    }

    /**
     * HTTP endpoint gives back single league
     * @param leagueId leagueId
     * @return HTTP response with single league stored in system
     */
    @GetMapping("/{leagueId}")
    public ResponseEntity<UserLeagueResponse> getLeagueById(
            @PathVariable Long leagueId){
        return new ResponseEntity<>(leagueService.getSingleLeague(leagueId), HttpStatus.OK);
    }

    /**
     * HTTP endpoint gives back list of competitors associated with league
     * @param leagueId league id
     * @return HTTP response with list of competitors ordered by average-score descending
     */
    @GetMapping("/{leagueId}/competitors")
    public ResponseEntity<List<LeagueCompetitorRowTableResponse>> getLeagueCompetitors(
            @PathVariable Long leagueId){
        String leagueGunType = leagueService.getGunTypeForLeague(leagueId);
        return new ResponseEntity<>(
                leagueCompetitorsService.getLeagueCompetitorsByLeagueId(leagueId, leagueGunType),
                HttpStatus.OK);
    }

    /**
     * HTTP endpoint gets all league groups (with the same league name prefix)
     * @param gunType allow only to search for league groups in specified gunType
     * @return List of all league_group names in the system
     */
    @GetMapping("/groups")
    public ResponseEntity<List<String>> getLeagueGroupsByGunType(
            @RequestParam("gunType") String gunType
    ){
        return new ResponseEntity<>(
                leagueService.getLeagueAvailableGroups(gunType),
                HttpStatus.OK
        );
    }

    /**
     * HTTP endpoint generates leagues group summary with group status
     * @param groupLeaguePrefix league group name(prefix of all leagues which belongs to group)
     * @return league summary of league group state
     */
    @GetMapping("/groups/{groupLeaguePrefix}")
    public ResponseEntity<LeagueGroupRelocationResponse> getGroupLeaguesSummary(
            @PathVariable String groupLeaguePrefix
    ){
        return new ResponseEntity<>(
                leagueService.leagueGroupsRelocationStatus(groupLeaguePrefix),
                HttpStatus.OK
        );
    }

    /**
     * HTTP endpoint updates average scores for
     * @param groupLeaguePrefix league group name(prefix of all leagues which belongs to group)
     * @return league summary of league group state
     */
    @PostMapping("/groups/{groupLeaguePrefix}/finish")
    public ResponseEntity<Void> updateLeaguesGroupsAverageScoresAndDelete(
            @PathVariable String groupLeaguePrefix
    ){
        leagueService.updateFinishedLeaguesGroupCompetitorsAverageScores(groupLeaguePrefix);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * HTTP endpoint generates leagues based on league size and group of users wanted to compete
     * with the same gunType
     * @param leagueSize max size of users in each league
     * @param competitorsGroupByGunType league groups gunTypes with list of users wanted to participate
     * @return list of LeagueGenerateResponse generated data based on input
     */
    @PostMapping("/generate")
    public ResponseEntity<List<LeagueGenerateResponse>> generateLeaguesForGunType(
            @RequestParam("leagueSize") Integer leagueSize,
            @RequestBody Map<String, List<Long>> competitorsGroupByGunType){

        return new ResponseEntity<>(
                leagueCompetitorsService.generateLeaguesBySize(competitorsGroupByGunType, leagueSize),
                HttpStatus.OK);
    }

    /**
     * HTTP endpoint saves leagues and leagues competitors
     * @param leaguesGroups object with leagues information and
     *                      each league have list of competitors
     * @return HTTP empty body response with successful operation
     */
    @PostMapping
    public ResponseEntity<Void> saveLeagues(@RequestBody SaveLeagueDto leaguesGroups) {
        leagueService.createLeaguesWithCompetitors(leaguesGroups);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * HTTP endpoint updates information about existing league
     * @param leagueId league id
     * @param updateLeagueDto league object with data to change in the league
     *                        (only league name can change)
     * @return HTTP response with changed details league
     */
    @PutMapping("/{leagueId}")
    public ResponseEntity<UserLeagueResponse> updateLeague(
            @PathVariable Long leagueId,
            @RequestBody UpdateLeagueDto updateLeagueDto){
        return new ResponseEntity<>(
                leagueService.updateLeague(leagueId, updateLeagueDto),
                HttpStatus.OK);
    }

    /**
     * HTTP endpoint deletes league and related league competitors from system
     * @param leagueId league id
     * @return HTTP empty body response with successful operation
     */
    @DeleteMapping("/{leagueId}")
    public ResponseEntity<Void> deleteLeagueAndLeagueCompetitors(@PathVariable Long leagueId){
        leagueService.deleteExistingLeague(leagueId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
