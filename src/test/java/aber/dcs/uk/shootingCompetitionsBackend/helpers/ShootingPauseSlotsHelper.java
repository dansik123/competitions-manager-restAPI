package aber.dcs.uk.shootingCompetitionsBackend.helpers;

import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingPauseSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class ShootingPauseSlotsHelper {
    public static List<ShootingPauseSlotEntity> threeCompetitorsMatchesPauseSlotsList(){
        UserEntity competitor1 = CredentialsHelper.getUserEntityById(3L);
        UserEntity competitor2 = CredentialsHelper.getUserEntityById(4L);
        UserEntity competitor3 = CredentialsHelper.getUserEntityById(5L);

        ShootingPauseSlotEntity slot1 = new ShootingPauseSlotEntity(
                1L, new LeagueEntity(1L),competitor1, 1,1);

        ShootingPauseSlotEntity slot2 = new ShootingPauseSlotEntity(
                2L, new LeagueEntity(1L),competitor3, 1, 2);

        ShootingPauseSlotEntity slot3 = new ShootingPauseSlotEntity(
                3L, new LeagueEntity(1L),competitor2, 1, 3);

        return new ArrayList<>(List.of(slot1, slot2, slot3));
    }

    public static ShootingPauseSlotEntity getSinglePauseMatchFromThreeCompetitorsMatches(Long id){
        return threeCompetitorsMatchesPauseSlotsList().
                stream().
                filter(slot -> slot.getId().equals(id)).
                findFirst().
                orElse(null);
    }
}
