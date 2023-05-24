package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.ImageMediaDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.LeaguesRelocateDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.ShootingResultDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.MatchDateDto;
import aber.dcs.uk.shootingCompetitionsBackend.responses.*;
import aber.dcs.uk.shootingCompetitionsBackend.services.LeaguesMatchesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/api")
public class LeaguesMatchesController {
    private final LeaguesMatchesService leaguesMatchesGeneratorService;

    public LeaguesMatchesController(
            LeaguesMatchesService leaguesMatchesGeneratorService) {
        this.leaguesMatchesGeneratorService = leaguesMatchesGeneratorService;
    }

    /**
     * HTTP GET method to display list of matches taking place in the league
     * only matches taking place in current league round
     * Only the current league round matches are shown
     * @param leagueId Long value identify all matches in league
     * @return List of matches played in current league round including information
     * about paused users
     */
    @GetMapping("/leagues/{leagueId}/matches")
    public ResponseEntity<List<SingleRoundMatchesResponse>> getAllLeagueMatches(
            @PathVariable Long leagueId){
        List<SingleRoundMatchesResponse> leagueMatches =
                leaguesMatchesGeneratorService.getMatchesGroupsBySlotRound(leagueId);
        return new ResponseEntity<>(
                leagueMatches,
                HttpStatus.OK
        );
    }

    /**
     * HTTP GET method to display all matches in leagues taking place across all league grounds
     * WITH CURRENT REQUEST SETUP CLIENT CAN GET GROUP OF LEAGUES NOT RELATED TO ONEONOTHER
     * AND PRODUCE SUMMARY. THIS IS WRONG. INSTEAD, ENDPOINT SHOULD GET LEAGUE GROUP PREFIX
     * AND DISPLAY THIS FULL SUMMARY FOR LEAGUE GROUP
     * @param leaguesIds list of league id's used to produce summary
     * @return summary contains all league matches with information who played against who,
     * match shooting scores, and LEAGUE round.
     * Summary also have total league points summed up per user from all points gained in the league
     */
    @GetMapping("/leagues/summary")
    public ResponseEntity<List<SummaryPointsRowResponse<LeagueRoundSingleSlotPoints>>> getSummaryForLeague(
            @RequestParam("leaguesIds") List<Long> leaguesIds) {
        return new ResponseEntity<>(
                leaguesMatchesGeneratorService.getAllLeaguesGroupStats(leaguesIds),
                HttpStatus.OK);
    }

    /**
     * HTTP GET method to display matches in leagues for current league round
     * @param leagueId league id for matches summary should be calculated
     * @return summary contains all league matches with information who played against who,
     * match shooting scores, and MATCH round.
     * Summary also have total league points summed up per user from all points gained in the league
     */
    @GetMapping("/leagues/{leagueId}/summary")
    public ResponseEntity<List<SummaryPointsRowResponse<MatchRoundSingleSlotPoints>>> getSummaryForLeague(
            @PathVariable Long leagueId) {
        return new ResponseEntity<>(
                leaguesMatchesGeneratorService.getLeagueStats(leagueId),
                HttpStatus.OK);
    }

    /**
     * HTTP POST method to create league matches for current league round
     * Use ROUND ROBIN MATCH PARING SOLUTION
     * @param leagueId league id for which matches will get generated
     * @return CREATED status without any response data if matches have been generated without any problems
     */
    @PostMapping("/leagues/{leagueId}/matches/generate")
    public ResponseEntity<Void> generateMatchesForLeague(
            @PathVariable Long leagueId) {
        leaguesMatchesGeneratorService.generateLeagueMatches(leagueId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * HTTP POST method to relocate user with all their matches scores and points to new league
     * Per each league with the same league name prefix server relate 2 or 1 person
     * @param leaguesRelocateDto data required to relocation includes league group prefix and list of leagues ids
     * @return Accepted status without any response data if relocation was successful
     */
    @PostMapping("/leagues/relocate")
    public ResponseEntity<Void> relocateUsersByTheirsSummaryScores(
            @RequestBody LeaguesRelocateDto leaguesRelocateDto
    ){
        leaguesMatchesGeneratorService.relocateAndPromotionsProcedure(
                leaguesRelocateDto.getLeaguesGroupPrefix(),
                leaguesRelocateDto.getGroupAllLeaguesIds());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * HTTP DELETE method to remove all matches related with league
     * includes deletion of league points, league matches and paused users related with matches
     * league stays in the same place for new matches generation if needed
     * @param leagueId league id for which all matches gets deleted
     * @return Accepted status without any response data if deletion was successful
     */
    @DeleteMapping("/leagues/{leagueId}/matches")
    public ResponseEntity<Void> deleteLeagueMatches(
            @PathVariable Long leagueId) {
        leaguesMatchesGeneratorService.deleteAllLeagueMatches(leagueId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * HTTP GET method to read single mach data by match id
     * @param matchId long match id
     * @return single match data with date fo the match slot, competitors details and their shooting
     * scores, points and scorecard images if any.
     */
    @GetMapping("/matches/{matchId}")
    public ResponseEntity<SingleMatchResponse> getSingleMatch(
            @PathVariable Long matchId) {
        return new ResponseEntity<>(
                leaguesMatchesGeneratorService.getSingleMatch(matchId),
                HttpStatus.OK);
    }

    /**
     * HTTP GET method to read list of matches in league for given user
     * @param leagueId long leagueId
     * @param userId long userId
     * @return list of all matches which user participate in given league
     */
    @GetMapping("/matches")
    public ResponseEntity<List<SingleMatchResponse>> getListOfMatchesAssignedToUser(
            @RequestParam("leagueId") Long leagueId,
            @RequestParam("userId") Long userId) {
        return new ResponseEntity<>(
                leaguesMatchesGeneratorService.getAllMatchesAssignedToUser(leagueId, userId),
                HttpStatus.OK);
    }

    /**
     * HTTP GET method to read competitor 1 scorecard
     * Excluded from any Authorization procedures
     * @param matchId match id which contains image
     * @return byte array containing scorecard image AS JPEG OR PNG
     * @throws IOException if there is problem to read of find image
     */
    @RequestMapping(value = "/matches/{matchId}/competitor1/scorecard", method = RequestMethod.GET,
        produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getScoreCardToCompetitor1InMatch(
            @PathVariable Long matchId) throws IOException {
        ImageMediaDto imageMediaDto = leaguesMatchesGeneratorService.getImageForMatchAndCompetitor(
                matchId, true);
        return ResponseEntity.ok()
                .contentType(imageMediaDto.getMediaType())
                .body(imageMediaDto.getImageContent());
    }

    /**
     * HTTP POST method to save scorecard image competitor 1
     * @param matchId to which match id assign image
     * @param file request Multipart file upload image
     * @return match single object with updated scorecard image filename
     * @throws IOException problem to save image on the server
     */
    @RequestMapping(value = "/matches/{matchId}/competitor1/scorecard", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SingleMatchResponse> addScoreCardToCompetitor1InMatch(
            @PathVariable Long matchId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(
                leaguesMatchesGeneratorService.addImageToCompetitor(matchId, true, file),
                HttpStatus.OK);
    }

    /**
     * HTTP GET method to read competitor 2 scorecard
     * Excluded from any Authorization procedures
     * @param matchId match id which contains image
     * @return byte array containing scorecard image AS JPEG OR PNG
     * @throws IOException if there is problem to read of find image
     */
    @RequestMapping(value = "/matches/{matchId}/competitor2/scorecard", method = RequestMethod.GET,
        produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<byte[]> getScoreCardToCompetitor2InMatch(
            @PathVariable Long matchId) throws IOException {
        ImageMediaDto imageMediaDto = leaguesMatchesGeneratorService.getImageForMatchAndCompetitor(
                matchId, false);
        return ResponseEntity.ok()
                .contentType(imageMediaDto.getMediaType())
                .body(imageMediaDto.getImageContent());
    }

    /**
     * HTTP POST method uploads new scorecard image to competitor2 of match
     * @param matchId match identifier
     * @param file multipart image file
     * @return match single object with updated file name of uploaded image scorecard
     * @throws IOException problem to upload image
     */
    @RequestMapping(value = "/matches/{matchId}/competitor2/scorecard", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SingleMatchResponse> addScoreCardToCompetitor2InMatch(
            @PathVariable Long matchId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(
                leaguesMatchesGeneratorService.addImageToCompetitor(matchId, false, file),
                HttpStatus.OK);
    }

    /**
     * HTTP PATH method to update match/slot date
     * @param matchId match identifier
     * @param dateDto match date data
     * @return match single object with updated date
     */
    @PatchMapping("/matches/{matchId}/date")
    public ResponseEntity<SingleMatchResponse> updateDateForMatch(
            @PathVariable Long matchId,
            @RequestBody MatchDateDto dateDto) {
        return new ResponseEntity<>(
                leaguesMatchesGeneratorService.changeMatchDate(matchId, dateDto),
                HttpStatus.OK);
    }

    /**
     * HTTP PATH method to update match shooting result
     * @param matchId match identifier
     * @param shootingResultDto shorting results for both competitors of the match
     * @return match single object with updated shooting results
     */
    @PatchMapping("/matches/{matchId}/result")
    public ResponseEntity<SingleMatchResponse> updateShootingResultsForMatch(
            @PathVariable Long matchId,
            @Valid @RequestBody ShootingResultDto shootingResultDto) {
        return new ResponseEntity<>(
                leaguesMatchesGeneratorService.updateSlotWithShootingResult(matchId, shootingResultDto),
                HttpStatus.OK);
    }

    /**
     * HTTP DELETE method to delete all matches in league related with user
     * @param leagueId league identifier
     * @param userId user identifier
     * @return response with no body
     */
    @DeleteMapping("/matches")
    public ResponseEntity<Void> deleteUserMatches(
            @RequestParam("leagueId") Long leagueId,
            @RequestParam("userId") Long userId) {
        leaguesMatchesGeneratorService.deleteAllUserMatches(leagueId, userId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
