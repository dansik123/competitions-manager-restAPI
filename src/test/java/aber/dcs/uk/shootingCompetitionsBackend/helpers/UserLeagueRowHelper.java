package aber.dcs.uk.shootingCompetitionsBackend.helpers;

import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;
import aber.dcs.uk.shootingCompetitionsBackend.responses.LeagueCompetitorRowTableResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.ScoreDetailsResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserAverageScoreResponse;

import java.math.BigDecimal;

public class UserLeagueRowHelper {
    public static LeagueCompetitorRowTableResponse getUserLeagueRowResponse(
            Long userId, GunType gunType){
        //get user
        UserEntity user = CredentialsHelper.getUserEntityById(userId);
        //get All User Average scores
        UserAverageScoreResponse averageScoreResponse =
                AverageScoresHelper.getAverageScoreResponseByUserId(userId);
        //get average score by GunType
        BigDecimal userPistolAverageScore =
                averageScoreResponse.
                        getScoresDetails().stream().
                        filter(score -> score.getGunType().
                                equals(gunType.name())).
                        findFirst().orElse(
                                new ScoreDetailsResponse(null, BigDecimal.valueOf(-1L))).
                        getAvgScore();
        return new LeagueCompetitorRowTableResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                userPistolAverageScore);
    }
}
