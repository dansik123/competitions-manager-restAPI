package aber.dcs.uk.shootingCompetitionsBackend.helpers;

import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubMemberEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class ClubMembersHelper {
    public ClubMemberEntity clubMember;

    public ClubMembersHelper() {
        clubMember = new ClubMemberEntity();
    }

    public ClubMembersHelper addCLub(Long id){
        clubMember.setClub(ClubHelper.getClubById(id));
        return this;
    }

    public ClubMembersHelper addMember(UserEntity user){
        clubMember.setMember(user);
        return this;
    }

    public ClubMemberEntity build(){
        return clubMember;
    }

    public static class ClubHelper{

        public static List<ClubEntity> getClubList(){
            return new ArrayList<>(List.of(
                    getAberystythClub(),
                    getWolverhamptonClub(),
                    getCardiffClub(),
                    getSwanseaClub()
            ));
        }

        public static ClubEntity getClubById(Long id){
            return getClubList().stream().filter(
                    club -> club.getId().equals(id)).findFirst().orElse(null);
        }

        public static ClubEntity getAberystythClub(){
            return new ClubEntity(
                    1L,
                    "Aberystwyth Club"
            );
        }
        public static ClubEntity getWolverhamptonClub(){
            return new ClubEntity(
                    2L,
                    "Wolverhampton Club"
            );
        }

        public static ClubEntity getCardiffClub(){
            return new ClubEntity(
                    3L,
                    "Cardiff Club"
            );
        }

        public static ClubEntity getSwanseaClub(){
            return new ClubEntity(
                    4L,
                    "Swansea Club"
            );
        }
    }
}
