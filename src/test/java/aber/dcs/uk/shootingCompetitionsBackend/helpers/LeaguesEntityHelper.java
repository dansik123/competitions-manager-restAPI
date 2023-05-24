package aber.dcs.uk.shootingCompetitionsBackend.helpers;

import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;

import java.util.List;

public class LeaguesEntityHelper {

    public static LeagueEntity getLeagueEntityById(Long leagueId){
        return getUsersEntitiesList().stream().
                filter(entity -> entity.getId().equals(leagueId)).
                findFirst().orElse(null);
    }


    private static List<LeagueEntity> getUsersEntitiesList(){
        return List.of(
                leagueId1Entity(),
                leagueId2Entity(),
                leagueId3Entity(),
                leagueId4Entity()
        );
    }
    private static LeagueEntity leagueId1Entity(){
        return new LeagueEntity(1L, "PISTOL_LEAGUE_1",2,2, 2, 1,
                false, GunType.PISTOL, List.of(), List.of(), List.of());
    }

    private static LeagueEntity leagueId2Entity(){
        return new LeagueEntity(2L, "PISTOL_LEAGUE_2",2,2, 2, 1,
                false, GunType.PISTOL, List.of(), List.of(), List.of());
    }

    private static LeagueEntity leagueId3Entity(){
        return new LeagueEntity(3L, "PISTOL_OTHER_LEAGUE_1",2,2, 2, 1,
                false, GunType.PISTOL, List.of(), List.of(), List.of());
    }

    private static LeagueEntity leagueId4Entity(){
        return new LeagueEntity(4L, "PISTOL_OTHER_LEAGUE_2",2,2, 2, 1,
                false, GunType.PISTOL, List.of(), List.of(), List.of());
    }

}
