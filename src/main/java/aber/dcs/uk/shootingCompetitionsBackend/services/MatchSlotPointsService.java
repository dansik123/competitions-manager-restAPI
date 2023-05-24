package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.dao.*;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.MatchSlotPointsEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.CustomHttpException;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.MatchSlotPointsRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.LeagueRoundSingleSlotPoints;
import aber.dcs.uk.shootingCompetitionsBackend.responses.MatchRoundSingleSlotPoints;
import aber.dcs.uk.shootingCompetitionsBackend.responses.SummaryPointsRowResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserMemberResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchSlotPointsService {
    private final MatchSlotPointsRepository matchSlotPointsRepository;
    private static final Integer DEFAULT_POINTS_FOR_GENERATION = null;
    private static final Integer WIN_POINTS = 2;
    private static final Integer LOSE_POINTS = 0;
    private static final Integer DRAW_POINTS = 1;

    public MatchSlotPointsService(MatchSlotPointsRepository matchSlotPointsRepository) {
        this.matchSlotPointsRepository = matchSlotPointsRepository;
    }

    /**
     * Methods adds new points records to database for each match/slot, 1 per each competitor in match(2)
     * @param savedSlots match/slots entities saved in database
     * @param league league entity saved in database
     */
    public void generateSlotsShootingPoints(List<ShootingSlotEntity> savedSlots, LeagueEntity league) {
        List<MatchSlotPointsEntity> leagueSlotPoints = new ArrayList<>(savedSlots.size() * 2);

        for(ShootingSlotEntity shootingSlot: savedSlots){
            leagueSlotPoints.add(
                    new MatchSlotPointsEntity(shootingSlot.getCompetitor1().getId(),shootingSlot.getId(),
                            league, DEFAULT_POINTS_FOR_GENERATION)
            ); //add default points to competitor 1 from slot

            leagueSlotPoints.add(
                    new MatchSlotPointsEntity(shootingSlot.getCompetitor2().getId(),shootingSlot.getId(),
                            league, DEFAULT_POINTS_FOR_GENERATION)
            ); //add default points to competitor 2 from slot
        }

        matchSlotPointsRepository.saveAll(leagueSlotPoints);
    }

    /**
     * Method updates league points for single match/slot based on match results
     * 2 WIN
     * 1 DRAW
     * 0 LOSE
     * @param updatedSlot match/slot entity saved in database
     */
    public void updateSlotShootingPoints(ShootingSlotEntity updatedSlot){

        if(updatedSlot == null){
            throw new CustomHttpException("Saved shooting slot is null", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(!updatedSlot.getHasScoreResult() ||
                updatedSlot.getCompetitor1Score() == null || updatedSlot.getCompetitor2Score() == null) {
                throw new CustomHttpException(
                        "Saved shooting slot does not have required field to set match points" +
                        "(competitor1Result, competitor2Result or score result was not set)",
                        HttpStatus.CONFLICT);
        }

        Long competitor1Id = updatedSlot.getCompetitor1().getId();
        Long competitor2Id = updatedSlot.getCompetitor2().getId();
        if(updatedSlot.getCompetitor1Score() > updatedSlot.getCompetitor2Score()){
            matchSlotPointsRepository.updateLeagueMatchSlotPoints(
                    competitor1Id, updatedSlot.getId(), WIN_POINTS);
            matchSlotPointsRepository.updateLeagueMatchSlotPoints(
                    competitor2Id, updatedSlot.getId(), LOSE_POINTS);
        }else if(updatedSlot.getCompetitor1Score() < updatedSlot.getCompetitor2Score()){
            matchSlotPointsRepository.updateLeagueMatchSlotPoints(
                    competitor1Id, updatedSlot.getId(), LOSE_POINTS);
            matchSlotPointsRepository.updateLeagueMatchSlotPoints(
                    competitor2Id, updatedSlot.getId(), WIN_POINTS);
        }else{
            matchSlotPointsRepository.updateLeagueMatchSlotPoints(
                    competitor1Id, updatedSlot.getId(), DRAW_POINTS);
            matchSlotPointsRepository.updateLeagueMatchSlotPoints(
                    competitor2Id, updatedSlot.getId(), DRAW_POINTS);
        }
    }

    /**
     * Method produce league group summary by getting all matches and total league points
     * @param userMemberDaoList league group all competitors
     * @param leaguesIds league group, the list of leagues identifiers
     * @return league group summary list data (individual matches info with total league points)
     */
    public List<SummaryPointsRowResponse<LeagueRoundSingleSlotPoints>> generateSummaryAllLeaguesGroupTable(
            List<UserMemberDao> userMemberDaoList, List<Long> leaguesIds){

        Map<Long, SummaryPointsRowResponse<LeagueRoundSingleSlotPoints>> summaryMap =
                new HashMap<>(userMemberDaoList.size());
        Map<Long, UserMemberResponse> usersMap = new HashMap<>(userMemberDaoList.size());

        //fill up map user keys
        for(UserMemberDao leagueUser: userMemberDaoList){
            UserMemberResponse currentUser = new UserMemberResponse(leagueUser);
            summaryMap.put(
                    leagueUser.getUserId(),
                    new SummaryPointsRowResponse<>(currentUser));
            usersMap.put(leagueUser.getUserId(), currentUser);
        }

        List<AllLeaguesPointsDao> leagueSlotPoints =
                matchSlotPointsRepository.getAllPointsWitIdsUsersForAllLeaguesRounds(leaguesIds);

        Long leaguePointsOwner;
        //fill up map wit individual slot league points for each user
        for(AllLeaguesPointsDao singleLeagueSlotPoints: leagueSlotPoints){
            leaguePointsOwner = singleLeagueSlotPoints.getSlotPointsOwnerId();
            SummaryPointsRowResponse<LeagueRoundSingleSlotPoints> userPoints = summaryMap.get(leaguePointsOwner);

            Integer slotPoints = singleLeagueSlotPoints.getSlotPoints();
            userPoints.getIndividualPoints().add(new LeagueRoundSingleSlotPoints(
                    slotPoints,
                    singleLeagueSlotPoints.getLeagueRoundNumber(),
                    usersMap.get(singleLeagueSlotPoints.getSlotMatchCompetitor1Id()),
                    usersMap.get(singleLeagueSlotPoints.getSlotMatchCompetitor2Id()),
                    singleLeagueSlotPoints.getSlotShootingScore()
            ));
            userPoints.addToTotalPoints(slotPoints);
        }

        return summaryMap.values()
                .stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    /**
     * Method gets summary of one league round matches in one league
     * @param leagueId leagueId league identifier
     * @param leagueCurrentRound league current round number
     * @param userMemberDaoList list of user competing in current league round
     * @return league summary list data (individual matches info with total league points[FOR ALL LEAGUE ROUNDS])
     */
    public List<SummaryPointsRowResponse<MatchRoundSingleSlotPoints>> generateSummaryOneLeaguesTable(
            Long leagueId, Integer leagueCurrentRound, List<UserMemberDao> userMemberDaoList){

        Map<Long, SummaryPointsRowResponse<MatchRoundSingleSlotPoints>> summaryMap = new HashMap<>(userMemberDaoList.size());
        Map<Long, UserMemberResponse> usersMap = new HashMap<>(userMemberDaoList.size());


        //fill up map user keys
        for(UserMemberDao leagueUser: userMemberDaoList){
            UserMemberResponse currentUser = new UserMemberResponse(leagueUser);
            summaryMap.put(
                    leagueUser.getUserId(),
                    new SummaryPointsRowResponse<>(currentUser)
            );
            usersMap.put(leagueUser.getUserId(), currentUser);
        }

        List<LeagueSlotPointsDao> leagueSlotPoints =
                matchSlotPointsRepository.getAllPointsWitIdsUsersForCurrentLeagueRound(leagueId, leagueCurrentRound);

        Long leaguePointsOwner;
        //fill up map wit individual slot league points for each user
        for(LeagueSlotPointsDao singleLeagueSlotPoints: leagueSlotPoints){
            leaguePointsOwner = singleLeagueSlotPoints.getSlotPointsOwnerId();
            SummaryPointsRowResponse<MatchRoundSingleSlotPoints> userPoints = summaryMap.get(leaguePointsOwner);

            userPoints.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                    singleLeagueSlotPoints.getSlotPoints(),
                    singleLeagueSlotPoints.getRoundNumber(),
                    usersMap.get(singleLeagueSlotPoints.getSlotMatchCompetitor1Id()),
                    usersMap.get(singleLeagueSlotPoints.getSlotMatchCompetitor2Id()),
                    singleLeagueSlotPoints.getSlotShootingScore()
            ));
        }
        List<LeaguePointsSummaryDao> leagueTotalPointsPerUser =
                matchSlotPointsRepository.getTotalPointsWitIdsUsersForLeague(leagueId);

        //fill up individual user data with total of their league points
        for(LeaguePointsSummaryDao singleTotalPointsWithUser: leagueTotalPointsPerUser){
            SummaryPointsRowResponse<MatchRoundSingleSlotPoints> userRowData = summaryMap.get(singleTotalPointsWithUser.getPointsOwnerId());
            userRowData.setTotalLeaguePoints(singleTotalPointsWithUser.getLeagueTotalPoints());
        }

        return summaryMap.values()
                .stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    /**
     * Method deletes all league match points related with league identifier
     * @param leagueId league identifier
     */
    public void deleteShootingSlotsRelatedPointsByLeagueId(Long leagueId){
        matchSlotPointsRepository.deleteAllLeagueSlotsPointsByLeagueId(leagueId);
    }

    /**
     * Methods gets for bottom league1 and top league2 competitors for relocation
     * @param leagueId1 league1 identifier
     * @param leagueId2 league2 identifier
     * @param howMany how many top and bottom players want to select
     * @return list of users to relocate
     */
    public List<LeagueRelocateUsersDao> getUsersToRelocate(
            Long leagueId1, Long leagueId2, Integer howMany){
        return matchSlotPointsRepository.getBottomLeague1PlayersAndTopLeague2PlayersWithLeaguePoints(
                leagueId1, leagueId2, howMany);
    }

    /**
     * Method changes points for relocated user from one league to another
     * @param newLeagueId new league identifier
     * @param oldLeagueId previous league identifier
     * @param userId user identifier
     */
    public void moveUserPointsToDifferentLeague(Long newLeagueId, Long oldLeagueId, Long userId){
        matchSlotPointsRepository.updateLeagueIdForOneUserAllPoints(newLeagueId, oldLeagueId, userId);
    }
}
