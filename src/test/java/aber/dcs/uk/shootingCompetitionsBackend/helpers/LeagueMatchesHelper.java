package aber.dcs.uk.shootingCompetitionsBackend.helpers;

import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingPauseSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.responses.MatchResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.SingleRoundMatchesResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserMemberResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LeagueMatchesHelper {
    public static List<ShootingSlotEntity> threeCompetitorsMatchesSlotsList(){
        UserEntity competitor1 = CredentialsHelper.getUserEntityById(3L);
        UserEntity competitor2 = CredentialsHelper.getUserEntityById(4L);
        UserEntity competitor3 = CredentialsHelper.getUserEntityById(5L);
        //round 1 slots
        ShootingSlotEntity slot1 = new ShootingSlotEntity(
                1L, new LeagueEntity(1L),competitor3, competitor2,
                null,1, 1, false,
                null, null, null, null);

        //round 2 slots
        ShootingSlotEntity slot2 = new ShootingSlotEntity(
                2L, new LeagueEntity(1L),competitor1, competitor2,
                null, 1, 2, false,
                null, null, null, null);

        //round 3 slots
        ShootingSlotEntity slot3 = new ShootingSlotEntity(
                3L, new LeagueEntity(1L),competitor1, competitor3,
                null, 1, 3, false,
                null, null, null, null);

        return new ArrayList<>(List.of(slot1, slot2, slot3));
    }

    public static List<ShootingSlotEntity> threeCompetitorsMatchesSlotsWithScoresList(){
        List<ShootingSlotEntity> list = threeCompetitorsMatchesSlotsList();
        //round 1 scores
        //match 1 scores
        list.get(0).setCompetitor1Score(2);
        list.get(0).setCompetitor2Score(0);
        //round 2 scores
        //match 2 scores
        list.get(1).setCompetitor1Score(0);
        list.get(1).setCompetitor2Score(2);

        //round 3 scores
        //match 3 scores
        list.get(2).setCompetitor1Score(2);
        list.get(2).setCompetitor2Score(0);

        return list;
    }

    public static List<ShootingSlotEntity> fourCompetitorsMatchesSlotsList(){
        UserEntity competitor1 = CredentialsHelper.getUserEntityById(3L);
        UserEntity competitor2 = CredentialsHelper.getUserEntityById(4L);
        UserEntity competitor3 = CredentialsHelper.getUserEntityById(5L);
        UserEntity competitor4 = CredentialsHelper.getUserEntityById(6L);
        //round 1 slots
        ShootingSlotEntity slot1 = new ShootingSlotEntity(
                1L, new LeagueEntity(1L),competitor1,competitor4,
                null, 1, 1, false,
                null, null, null, null);
        ShootingSlotEntity slot2 = new ShootingSlotEntity(
                2L, new LeagueEntity(1L),competitor3, competitor2,
                null, 1, 1, false,
                null, null, null, null);

        //round 2 slots
        ShootingSlotEntity slot3 = new ShootingSlotEntity(
                3L, new LeagueEntity(1L),competitor1, competitor2,
                null, 1, 2, false,
                null, null, null, null);
        ShootingSlotEntity slot4 = new ShootingSlotEntity(
                4L, new LeagueEntity(1L), competitor4, competitor3,
                null, 1, 2, false,
                null, null, null, null);

        //round 3 slots
        ShootingSlotEntity slot5 = new ShootingSlotEntity(
                5L, new LeagueEntity(1L),competitor1, competitor3,
                null, 1, 3, false,
                null, null, null, null);
        ShootingSlotEntity slot6 = new ShootingSlotEntity(
                6L, new LeagueEntity(1L),competitor2, competitor4,
                null, 1, 3, false,
                null, null, null, null);

        return new ArrayList<>(List.of(slot1, slot2, slot3, slot4, slot5, slot6));
    }

    public static List<ShootingSlotEntity> fourCompetitorsMatchesSlotsWithScoresList(){
        List<ShootingSlotEntity> list = fourCompetitorsMatchesSlotsList();
        //round 1 scores
        //match 1 scores
        list.get(0).setCompetitor1Score(1);
        list.get(0).setCompetitor2Score(1);
        //match 2 scores
        list.get(1).setCompetitor1Score(2);
        list.get(1).setCompetitor2Score(0);

        //round 2 scores
        //match 3 scores
        list.get(2).setCompetitor1Score(2);
        list.get(2).setCompetitor2Score(0);

        //match 4 scores
        list.get(3).setCompetitor1Score(1);
        list.get(3).setCompetitor2Score(1);

        //round 3 scores
        //match 5 scores
        list.get(4).setCompetitor1Score(0);
        list.get(4).setCompetitor2Score(2);

        //match 6 scores
        list.get(5).setCompetitor1Score(2);
        list.get(5).setCompetitor2Score(0);

        return list;
    }
    public static List<ShootingSlotEntity> threeCompetitorsMatchesWithMatchIds(){
        AtomicInteger count=new AtomicInteger(1);
        return threeCompetitorsMatchesSlotsList().stream().
                peek(slot -> {
                slot.setId(count.longValue());
                count.incrementAndGet();
                }
            ).
            collect(Collectors.toList());
    }

    public static List<ShootingSlotEntity> fourCompetitorsMatchesWithMatchIds(){
        AtomicInteger count=new AtomicInteger(1);
        return fourCompetitorsMatchesSlotsList().stream().
                peek(slot -> {
                            slot.setId(count.longValue());
                            count.incrementAndGet();
                        }
                ).
                collect(Collectors.toList());
    }

    public static List<ShootingSlotEntity> getSlotsWith2CompetitorsByLeagueAndUserIdThreeLeagueCompetitors(
            Long leagueId, Long userId){
        return threeCompetitorsMatchesWithMatchIds().stream()
            .filter(
                slot -> slot.getLeague().getId().equals(leagueId)
                        && (slot.getCompetitor1().getId().equals(userId) ||
                                slot.getCompetitor2().getId().equals(userId))
            )
            .sorted(Comparator.comparing(ShootingSlotEntity::getMatchRoundNumber))
            .collect(Collectors.toList());
    }

    public static List<SingleRoundMatchesResponse> getInMemoryListOfMatchesResponse(int numberOfCompetitors){
        List<ShootingSlotEntity> shootingSlots;
        List<ShootingPauseSlotEntity> shootingWaitingSlots;
        if(numberOfCompetitors == 3){
            shootingSlots = threeCompetitorsMatchesWithMatchIds().stream().
                    sorted(Comparator.comparing(ShootingSlotEntity::getMatchRoundNumber)).
                    collect(Collectors.toList());

            shootingWaitingSlots = ShootingPauseSlotsHelper.
                    threeCompetitorsMatchesPauseSlotsList().stream().
                    sorted(Comparator.comparing(ShootingPauseSlotEntity::getMatchRoundNumber)).
                    collect(Collectors.toList());
        }else if(numberOfCompetitors == 4){
            shootingSlots = fourCompetitorsMatchesWithMatchIds().stream().
                    sorted(Comparator.comparing(ShootingSlotEntity::getMatchRoundNumber)).
                    collect(Collectors.toList());

            shootingWaitingSlots = new ArrayList<>();
        }else{
            shootingSlots = new ArrayList<>();

            shootingWaitingSlots = new ArrayList<>();
        }

        List<SingleRoundMatchesResponse> data = new ArrayList<>();
        for(int i=1; !shootingSlots.isEmpty(); i++){
            List<MatchResponse> roundMatches = new ArrayList<>();
            List<UserMemberResponse> waitUsers = new ArrayList<>();

            while(!shootingSlots.isEmpty() && shootingSlots.get(0).getMatchRoundNumber().equals(i)){
                roundMatches.add(new MatchResponse(shootingSlots.get(0)));
                shootingSlots.remove(shootingSlots.get(0));
            }

            while(!shootingWaitingSlots.isEmpty() && shootingWaitingSlots.get(0).getMatchRoundNumber().equals(i)){
                waitUsers.add(new UserMemberResponse(shootingWaitingSlots.get(0).getCompetitor()));
                shootingWaitingSlots.remove(shootingWaitingSlots.get(0));
            }

            data.add(new SingleRoundMatchesResponse(i, waitUsers, roundMatches));
        }

        return data;
    }

    public static ShootingSlotEntity getSingleMatchFromThreeCompetitorsMatches(Long id){
        return threeCompetitorsMatchesWithMatchIds().
                stream().
                filter(slot -> slot.getId().equals(id)).
                findFirst().
                orElse(null);
    }

    public static ShootingSlotEntity getSingleMatchFromFourCompetitorsMatches(Long id){
        return fourCompetitorsMatchesWithMatchIds().
                stream().
                filter(slot -> slot.getId().equals(id)).
                findFirst().
                orElse(null);
    }

    public static List<SingleRoundMatchesResponse> threeCompetitorsMatchesManual(){
        ShootingSlotEntity round1match = getSingleMatchFromThreeCompetitorsMatches(1L);
        ShootingSlotEntity round2match = getSingleMatchFromThreeCompetitorsMatches(2L);
        ShootingSlotEntity round3match = getSingleMatchFromThreeCompetitorsMatches(3L);

        ShootingPauseSlotEntity round1waitSlot = ShootingPauseSlotsHelper.
                getSinglePauseMatchFromThreeCompetitorsMatches(1L);
        ShootingPauseSlotEntity round2waitSlot = ShootingPauseSlotsHelper.
                getSinglePauseMatchFromThreeCompetitorsMatches(2L);
        ShootingPauseSlotEntity round3waitSlot = ShootingPauseSlotsHelper.
                getSinglePauseMatchFromThreeCompetitorsMatches(3L);

        SingleRoundMatchesResponse round1Response = new SingleRoundMatchesResponse(
                1,
                new ArrayList<>(List.of(new UserMemberResponse(round1waitSlot.getCompetitor()))),
                new ArrayList<>(List.of(new MatchResponse(round1match)))
        );

        SingleRoundMatchesResponse round2Response = new SingleRoundMatchesResponse(
                2,
                new ArrayList<>(List.of(new UserMemberResponse(round2waitSlot.getCompetitor()))),
                new ArrayList<>(List.of(new MatchResponse(round2match)))
        );

        SingleRoundMatchesResponse round3Response = new SingleRoundMatchesResponse(
                3,
                new ArrayList<>(List.of(new UserMemberResponse(round3waitSlot.getCompetitor()))),
                new ArrayList<>(List.of(new MatchResponse(round3match)))
        );

        return new ArrayList<>(List.of(round1Response, round2Response, round3Response));
    }

    public static List<SingleRoundMatchesResponse> fourCompetitorsMatchesManual(){
        ShootingSlotEntity round1match1 = getSingleMatchFromFourCompetitorsMatches(1L);
        ShootingSlotEntity round1match2 = getSingleMatchFromFourCompetitorsMatches(2L);
        ShootingSlotEntity round2match1 = getSingleMatchFromFourCompetitorsMatches(3L);
        ShootingSlotEntity round2match2 = getSingleMatchFromFourCompetitorsMatches(4L);
        ShootingSlotEntity round3match1 = getSingleMatchFromFourCompetitorsMatches(5L);
        ShootingSlotEntity round3match2 = getSingleMatchFromFourCompetitorsMatches(6L);


        SingleRoundMatchesResponse round1Response = new SingleRoundMatchesResponse(
                1,
                new ArrayList<>(),
                new ArrayList<>(List.of(
                        new MatchResponse(round1match1),
                        new MatchResponse(round1match2)
                        ))
        );

        SingleRoundMatchesResponse round2Response = new SingleRoundMatchesResponse(
                2,
                new ArrayList<>(),
                new ArrayList<>(List.of(
                        new MatchResponse(round2match1),
                        new MatchResponse(round2match2)
                ))
        );

        SingleRoundMatchesResponse round3Response = new SingleRoundMatchesResponse(
                3,
                new ArrayList<>(),
                new ArrayList<>(List.of(
                        new MatchResponse(round3match1),
                        new MatchResponse(round3match2)
                ))
        );

        return new ArrayList<>(List.of(round1Response, round2Response, round3Response));
    }
}
