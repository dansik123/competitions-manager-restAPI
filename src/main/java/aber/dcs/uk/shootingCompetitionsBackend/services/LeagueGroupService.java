package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ShootingSlotScoresSummaryDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.CustomHttpException;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.AverageShootingScoreRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.ShootingSlotRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.LeagueGroupRelocationResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.LeagueStatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LeagueGroupService {
    private final ShootingSlotRepository shootingSlotRepository;

    private final AverageShootingScoreRepository averageShootingScoreRepository;
    private final static String LEAGUE_MATCHES_NOT_GENERATED_STATUS = "Matches not generated";
    private final static String LEAGUE_MATCHES_NOT_FINISHED_STATUS = "Matches not finished";
    private final static String LEAGUE_MATCHES_WON_STATUS = "Matches done";

    public LeagueGroupService(ShootingSlotRepository shootingSlotRepository,
                              AverageShootingScoreRepository averageShootingScoreRepository) {
        this.shootingSlotRepository = shootingSlotRepository;
        this.averageShootingScoreRepository = averageShootingScoreRepository;
    }

    /**
     * Method check if all leagues in the list are in last round
     * @param leagues list of leagues to check
     * @return true if yes, no otherwise
     */
    public boolean areAllLeaguesInLastLeagueRound(List<LeagueEntity> leagues){
        return leagues
                .stream()
                .allMatch(league -> league.getTotalRoundsToPlay().equals(league.getCurrentRound()));
    }

    /**
     * Method generates status for league group
     * @param leaguesEntities list of leagues in the league group
     * @param leagueGroupNameFullPrefix league group name prefix like OCTOBER_PISTOL_LEAGUE
     * @return league group overall status with individual status for each league
     */
    public LeagueGroupRelocationResponse leaguesGroupStatus(
            List<LeagueEntity> leaguesEntities, String leagueGroupNameFullPrefix){

        boolean allLeaguesHaveLastRound = areAllLeaguesInLastLeagueRound(leaguesEntities);
        List<Long> leaguesIds = leaguesEntities
                .stream()
                .map(LeagueEntity::getId)
                .collect(Collectors.toList());
        Set<Long> unfinishedLeaguesSet = new HashSet<>(
                shootingSlotRepository.getLeaguesIdsByUnfinishedSlots(leaguesIds));

        List<LeagueStatusResponse> leaguesStatus = new ArrayList<>(leaguesEntities.size());
        boolean readyToRelocate = true;

        for(LeagueEntity league: leaguesEntities){
            if(!league.getHasGeneratedMatches()){
                leaguesStatus.add(
                        new LeagueStatusResponse(league, LEAGUE_MATCHES_NOT_GENERATED_STATUS));
                readyToRelocate = false;
                continue;
            }

            if(unfinishedLeaguesSet.contains(league.getId())){
                leaguesStatus.add(
                        new LeagueStatusResponse(league, LEAGUE_MATCHES_NOT_FINISHED_STATUS));
                readyToRelocate = false;
                continue;
            }

            leaguesStatus.add(
                    new LeagueStatusResponse(league, LEAGUE_MATCHES_WON_STATUS));
        }
        boolean isLeaguesGroupFinished = readyToRelocate && allLeaguesHaveLastRound;

        return new LeagueGroupRelocationResponse(leagueGroupNameFullPrefix, readyToRelocate, isLeaguesGroupFinished, leaguesStatus);
    }

    /**
     * Method changes average scores of all competitors across list of leagues
     * @param leaguesEntities list of leagues for which their user will get average score updated
     * @param leagueGroupNameFullPrefix league group name prefix string
     */
    public void updateAverageScoreCompetitors(
            List<LeagueEntity> leaguesEntities, String leagueGroupNameFullPrefix){

        LeagueGroupRelocationResponse leagueGroupStatus =
                leaguesGroupStatus(leaguesEntities, leagueGroupNameFullPrefix);
        if(!leagueGroupStatus.getLeagueGroupFinished()){
            throw new CustomHttpException(
                    "Some leagues are not finished yet, can't update average scores",
                    HttpStatus.CONFLICT);
        }

        List<Long> leaguesIds = leaguesEntities
                .stream()
                .map(LeagueEntity::getId)
                .toList();
        List<ShootingSlotScoresSummaryDao> sumScoresPerCompetitor =
                shootingSlotRepository.getScoresSumAndCountPerCompetitorInLeagueGroup(leaguesIds);

        String leaguesGunType = leaguesEntities.stream().findFirst().orElseThrow(
                () -> new CustomHttpException("No leagues",
                HttpStatus.CONFLICT)
        ).getGunType().name();

        for(ShootingSlotScoresSummaryDao userScoresSummary: sumScoresPerCompetitor){
            Long userId = userScoresSummary.getScoresOwnerId();
            float average = (float) userScoresSummary.getScoresSum() /
                    (userScoresSummary.getScoresCount());
            BigDecimal newAverage = new BigDecimal(average);
            averageShootingScoreRepository.updateUserAverageScoreByUserIdAndGunType(
                    userId, leaguesGunType, newAverage.setScale(2, RoundingMode.HALF_EVEN));
        }
    }
}
