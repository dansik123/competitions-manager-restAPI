package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;

import java.util.Objects;

public class LeagueStatusResponse {
    Long leagueId;
    String leagueName;
    String leagueStatus;

    public LeagueStatusResponse() {
    }

    public LeagueStatusResponse(LeagueEntity league, String leagueStatus) {
        this.leagueId = league.getId();
        this.leagueName = league.getName();
        this.leagueStatus = leagueStatus;
    }
    public LeagueStatusResponse(Long leagueId, String leagueName, String leagueStatus) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.leagueStatus = leagueStatus;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getLeagueStatus() {
        return leagueStatus;
    }

    public void setLeagueStatus(String leagueStatus) {
        this.leagueStatus = leagueStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueStatusResponse that = (LeagueStatusResponse) o;
        return Objects.equals(leagueId, that.leagueId) &&
                Objects.equals(leagueName, that.leagueName) &&
                Objects.equals(leagueStatus, that.leagueStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueId, leagueName, leagueStatus);
    }
}
