package aber.dcs.uk.shootingCompetitionsBackend.responses;

import java.util.Objects;

public class LeagueRoundSingleSlotPoints {
    private Integer slotLeaguePoints;
    private Integer leagueRoundNumber;
    private UserMemberResponse competitor1;
    private UserMemberResponse competitor2;
    private String slotShootingResult;

    public LeagueRoundSingleSlotPoints() {
    }

    public LeagueRoundSingleSlotPoints(Integer slotLeaguePoints, Integer leagueRoundNumber,
                                       UserMemberResponse competitor1, UserMemberResponse competitor2,
                                       String slotShootingResult) {
        this.slotLeaguePoints = slotLeaguePoints;
        this.leagueRoundNumber = leagueRoundNumber;
        this.competitor1 = competitor1;
        this.competitor2 = competitor2;
        this.slotShootingResult = slotShootingResult;
    }

    public Integer getSlotLeaguePoints() {
        return slotLeaguePoints;
    }

    public void setSlotLeaguePoints(Integer slotLeaguePoints) {
        this.slotLeaguePoints = slotLeaguePoints;
    }

    public Integer getLeagueRoundNumber() {
        return leagueRoundNumber;
    }

    public void setLeagueRoundNumber(Integer leagueRoundNumber) {
        this.leagueRoundNumber = leagueRoundNumber;
    }

    public UserMemberResponse getCompetitor1() {
        return competitor1;
    }

    public void setCompetitor1(UserMemberResponse competitor1) {
        this.competitor1 = competitor1;
    }

    public UserMemberResponse getCompetitor2() {
        return competitor2;
    }

    public void setCompetitor2(UserMemberResponse competitor2) {
        this.competitor2 = competitor2;
    }

    public String getSlotShootingResult() {
        return slotShootingResult;
    }

    public void setSlotShootingResult(String slotShootingResult) {
        this.slotShootingResult = slotShootingResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueRoundSingleSlotPoints that = (LeagueRoundSingleSlotPoints) o;
        return Objects.equals(slotLeaguePoints, that.slotLeaguePoints) &&
                Objects.equals(leagueRoundNumber, that.leagueRoundNumber) &&
                Objects.equals(competitor1, that.competitor1) &&
                Objects.equals(competitor2, that.competitor2) &&
                Objects.equals(slotShootingResult, that.slotShootingResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotLeaguePoints, leagueRoundNumber,
                competitor1, competitor2, slotShootingResult);
    }
}
