package aber.dcs.uk.shootingCompetitionsBackend.helpers;

import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.MatchSlotPointsEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.responses.MatchRoundSingleSlotPoints;
import aber.dcs.uk.shootingCompetitionsBackend.responses.SummaryPointsRowResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserMemberResponse;

import java.util.ArrayList;
import java.util.List;

public class MatchSlotPointsHelper {
    public static List<MatchSlotPointsEntity> getMatchSlotPointsForSlotsWithThreeCompetitors(Long leagueId){
        ShootingSlotEntity slot1 = LeagueMatchesHelper.getSingleMatchFromThreeCompetitorsMatches(1L);
        ShootingSlotEntity slot2 = LeagueMatchesHelper.getSingleMatchFromThreeCompetitorsMatches(2L);
        ShootingSlotEntity slot3 = LeagueMatchesHelper.getSingleMatchFromThreeCompetitorsMatches(3L);
        LeagueEntity league = LeaguesEntityHelper.getLeagueEntityById(1L);

        UserEntity competitor1 = CredentialsHelper.getUserEntityById(3L);
        UserEntity competitor2 = CredentialsHelper.getUserEntityById(4L);
        UserEntity competitor3 = CredentialsHelper.getUserEntityById(5L);
        //round1Points
        MatchSlotPointsEntity pointsMatch1_1 = new MatchSlotPointsEntity(
                competitor3.getId(), slot1.getId(), competitor3, slot1, league, 2
        );
        MatchSlotPointsEntity pointsMatch1_2 = new MatchSlotPointsEntity(
                competitor2.getId(), slot1.getId(), competitor2, slot1, league, 0
        );

        //round1Points
        MatchSlotPointsEntity pointsMatch2_1 = new MatchSlotPointsEntity(
                competitor1.getId(), slot2.getId(), competitor1, slot2, league, 2
        );
        MatchSlotPointsEntity pointsMatch2_2 = new MatchSlotPointsEntity(
                competitor2.getId(), slot2.getId(), competitor2, slot2, league, 0
        );

        //round1Points
        MatchSlotPointsEntity pointsMatch3_1 = new MatchSlotPointsEntity(
                competitor1.getId(), slot3.getId(), competitor1, slot3, league, 2
        );
        MatchSlotPointsEntity pointsMatch3_2 = new MatchSlotPointsEntity(
                competitor3.getId(), slot3.getId(), competitor3, slot3, league, 0
        );

        return new ArrayList<>(List.of(
                pointsMatch1_1, pointsMatch1_2,
                pointsMatch2_1, pointsMatch2_2,
                pointsMatch3_1, pointsMatch3_2
        ));
    }

    public static List<MatchSlotPointsEntity> getMatchSlotPointsForSlotsWithFourCompetitors(Long leagueId){
        ShootingSlotEntity slot1 = LeagueMatchesHelper.getSingleMatchFromFourCompetitorsMatches(1L);
        ShootingSlotEntity slot2 = LeagueMatchesHelper.getSingleMatchFromFourCompetitorsMatches(2L);
        ShootingSlotEntity slot3 = LeagueMatchesHelper.getSingleMatchFromFourCompetitorsMatches(3L);
        ShootingSlotEntity slot4 = LeagueMatchesHelper.getSingleMatchFromFourCompetitorsMatches(4L);
        ShootingSlotEntity slot5 = LeagueMatchesHelper.getSingleMatchFromFourCompetitorsMatches(5L);
        ShootingSlotEntity slot6 = LeagueMatchesHelper.getSingleMatchFromFourCompetitorsMatches(6L);

        LeagueEntity league = LeaguesEntityHelper.getLeagueEntityById(1L);

        UserEntity competitor1 = CredentialsHelper.getUserEntityById(3L);
        UserEntity competitor2 = CredentialsHelper.getUserEntityById(4L);
        UserEntity competitor3 = CredentialsHelper.getUserEntityById(5L);
        UserEntity competitor4 = CredentialsHelper.getUserEntityById(6L);

        //round1Points
        MatchSlotPointsEntity pointsMatch1_1 = new MatchSlotPointsEntity(
                competitor1.getId(), slot1.getId(), competitor1, slot1, league, 2
        );
        MatchSlotPointsEntity pointsMatch1_2 = new MatchSlotPointsEntity(
                competitor4.getId(), slot1.getId(), competitor4, slot1, league, 0
        );
        MatchSlotPointsEntity pointsMatch2_1 = new MatchSlotPointsEntity(
                competitor3.getId(), slot2.getId(), competitor3, slot2, league, 1
        );
        MatchSlotPointsEntity pointsMatch2_2 = new MatchSlotPointsEntity(
                competitor2.getId(), slot2.getId(), competitor2, slot2, league, 1
        );

        //round2Points
        MatchSlotPointsEntity pointsMatch3_1 = new MatchSlotPointsEntity(
                competitor1.getId(), slot3.getId(), competitor1, slot3, league, 2
        );
        MatchSlotPointsEntity pointsMatch3_2 = new MatchSlotPointsEntity(
                competitor2.getId(), slot3.getId(), competitor2, slot3, league, 0
        );
        MatchSlotPointsEntity pointsMatch4_1 = new MatchSlotPointsEntity(
                competitor4.getId(), slot4.getId(), competitor4, slot4, league, 0
        );
        MatchSlotPointsEntity pointsMatch4_2 = new MatchSlotPointsEntity(
                competitor3.getId(), slot4.getId(), competitor3, slot4, league, 2
        );

        //round3Points
        MatchSlotPointsEntity pointsMatch5_1 = new MatchSlotPointsEntity(
                competitor1.getId(), slot5.getId(), competitor1, slot5, league, 2
        );
        MatchSlotPointsEntity pointsMatch5_2 = new MatchSlotPointsEntity(
                competitor3.getId(), slot5.getId(), competitor3, slot5, league, 0
        );
        MatchSlotPointsEntity pointsMatch6_1 = new MatchSlotPointsEntity(
                competitor2.getId(), slot6.getId(), competitor2, slot6, league, 2
        );
        MatchSlotPointsEntity pointsMatch6_2 = new MatchSlotPointsEntity(
                competitor4.getId(), slot6.getId(), competitor4, slot6, league, 0
        );

        return new ArrayList<>(List.of(
                pointsMatch1_1, pointsMatch1_2,
                pointsMatch2_1, pointsMatch2_2,
                pointsMatch3_1, pointsMatch3_2,
                pointsMatch4_1, pointsMatch4_2,
                pointsMatch5_1, pointsMatch5_2,
                pointsMatch6_1, pointsMatch6_2
        ));
    }

    public static List<SummaryPointsRowResponse<MatchRoundSingleSlotPoints>>expectedResponseFourCompetitorsMatches() {
        UserMemberResponse competitor1 = new UserMemberResponse(CredentialsHelper.getUserEntityById(3L));
        UserMemberResponse competitor2 = new UserMemberResponse(CredentialsHelper.getUserEntityById(4L));
        UserMemberResponse competitor3 = new UserMemberResponse(CredentialsHelper.getUserEntityById(5L));
        UserMemberResponse competitor4 = new UserMemberResponse(CredentialsHelper.getUserEntityById(6L));
        SummaryPointsRowResponse<MatchRoundSingleSlotPoints> summaryUser1 = new SummaryPointsRowResponse<>();
        //user1 Individual scores shooting slots are null but in real app they should be filled with appropriate scores from match table
        summaryUser1.setIndividualPoints(new ArrayList<>(3));
        summaryUser1.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                2, 1, competitor1, competitor4, null)
        );
        summaryUser1.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                2, 2, competitor1, competitor2, null)
        );
        summaryUser1.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                2, 3, competitor1, competitor3, null)
        );
        summaryUser1.setTotalLeaguePoints(6);
        summaryUser1.setPointsOwner(competitor1);

        //user2 data
        SummaryPointsRowResponse<MatchRoundSingleSlotPoints> summaryUser2 = new SummaryPointsRowResponse<>();
        summaryUser2.setIndividualPoints(new ArrayList<>(3));
        summaryUser2.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                1, 1, competitor3, competitor2, null)
        );
        summaryUser2.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                0, 2, competitor1, competitor2, null)
        );
        summaryUser2.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                2, 3, competitor2, competitor4, null)
        );
        summaryUser2.setTotalLeaguePoints(3);
        summaryUser2.setPointsOwner(competitor2);

        //user3 data
        SummaryPointsRowResponse<MatchRoundSingleSlotPoints> summaryUser3 = new SummaryPointsRowResponse<>();
        summaryUser3.setIndividualPoints(new ArrayList<>(3));
        summaryUser3.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                1, 1, competitor3, competitor2, null)
        );
        summaryUser3.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                2, 2, competitor4, competitor3, null)
        );
        summaryUser3.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                0, 3, competitor1, competitor3, null)
        );
        summaryUser3.setTotalLeaguePoints(3);
        summaryUser3.setPointsOwner(competitor3);

        //user4 data
        SummaryPointsRowResponse<MatchRoundSingleSlotPoints> summaryUser4 = new SummaryPointsRowResponse<>();
        summaryUser4.setIndividualPoints(new ArrayList<>(3));
        summaryUser4.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                0, 1, competitor1, competitor4, null)
        );
        summaryUser4.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                0, 2, competitor4, competitor3, null)
        );
        summaryUser4.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                0, 3, competitor2, competitor4, null)
        );
        summaryUser4.setTotalLeaguePoints(0);
        summaryUser4.setPointsOwner(competitor4);

        return new ArrayList<>(List.of(summaryUser1, summaryUser2, summaryUser3, summaryUser4));
    }

    public static List<SummaryPointsRowResponse<MatchRoundSingleSlotPoints>>expectedResponseThreeCompetitorsMatches() {
        UserMemberResponse competitor1 = new UserMemberResponse(CredentialsHelper.getUserEntityById(3L));
        UserMemberResponse competitor2 = new UserMemberResponse(CredentialsHelper.getUserEntityById(4L));
        UserMemberResponse competitor3 = new UserMemberResponse(CredentialsHelper.getUserEntityById(5L));
        SummaryPointsRowResponse<MatchRoundSingleSlotPoints> summaryUser1 = new SummaryPointsRowResponse<>();
        //user1 Individual scores shooting slots are null but in real app they should be filled with appropriate scores from match table
        summaryUser1.setIndividualPoints(new ArrayList<>(3));
        summaryUser1.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                2, 2, competitor1, competitor2, null)
        );
        summaryUser1.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                2, 3, competitor1, competitor3, null)
        );

        summaryUser1.setTotalLeaguePoints(4);
        summaryUser1.setPointsOwner(competitor1);

        //user2 data
        SummaryPointsRowResponse<MatchRoundSingleSlotPoints> summaryUser2 = new SummaryPointsRowResponse<>();
        summaryUser2.setIndividualPoints(new ArrayList<>(3));
        summaryUser2.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                0, 1, competitor3, competitor2, null)
        );
        summaryUser2.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                0, 2, competitor1, competitor2, null)
        );
        summaryUser2.setTotalLeaguePoints(0);
        summaryUser2.setPointsOwner(competitor2);

        //user3 data
        SummaryPointsRowResponse<MatchRoundSingleSlotPoints> summaryUser3 = new SummaryPointsRowResponse<>();
        summaryUser3.setIndividualPoints(new ArrayList<>(3));
        summaryUser3.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                2, 1, competitor3, competitor2, null)
        );
        summaryUser3.getIndividualPoints().add(new MatchRoundSingleSlotPoints(
                0, 3, competitor1, competitor3, null)
        );
        summaryUser3.setTotalLeaguePoints(2);
        summaryUser3.setPointsOwner(competitor3);

        return new ArrayList<>(List.of(summaryUser1, summaryUser3, summaryUser2));
    }
}
