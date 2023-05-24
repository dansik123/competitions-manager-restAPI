package aber.dcs.uk.shootingCompetitionsBackend.helpers;

import aber.dcs.uk.shootingCompetitionsBackend.responses.ScoreDetailsResponse;
import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserAverageScoreResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserMemberResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AverageScoresHelper {

    public static UserAverageScoreResponse getAverageScoreResponseByUserId(Long userId){
        return averageScoreEntities().stream().
                filter(element -> element.getUserId().equals(userId)).findFirst().orElse(null);
    }

    private static List<UserAverageScoreResponse> averageScoreEntities(){
        return List.of(
            new UserAverageScoreResponse(
                new UserMemberResponse(CredentialsHelper.getUserEntityById(3L)),
                new ArrayList<>(List.of(
                        new ScoreDetailsResponse(GunType.PISTOL.name(), new BigDecimal("0.94")),
                        new ScoreDetailsResponse(GunType.RIFLE.name(), new BigDecimal("0.58"))
                ))
            ),
            new UserAverageScoreResponse(
                    new UserMemberResponse(CredentialsHelper.getUserEntityById(4L)),
                    new ArrayList<>(List.of(
                            new ScoreDetailsResponse(GunType.PISTOL.name(), new BigDecimal("0.25")),
                            new ScoreDetailsResponse(GunType.RIFLE.name(), new BigDecimal("0.75"))
                    ))
            ),
            new UserAverageScoreResponse(
                    new UserMemberResponse(CredentialsHelper.getUserEntityById(5L)),
                    new ArrayList<>(List.of(
                            new ScoreDetailsResponse(GunType.PISTOL.name(), new BigDecimal("0.45"))
                    ))
            ),
            new UserAverageScoreResponse(
                    new UserMemberResponse(CredentialsHelper.getUserEntityById(6L)),
                    new ArrayList<>(List.of(
                            new ScoreDetailsResponse(GunType.PISTOL.name(), new BigDecimal("0.70"))
                    ))
            )
        );
    }
}
