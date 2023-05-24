package aber.dcs.uk.shootingCompetitionsBackend.responses;

import java.util.List;
import java.util.Objects;

public class LeagueGroupRelocationResponse {
    private String leagueGroupName;
    private Boolean isReadyToRelocate;
    private Boolean isLeagueGroupFinished;
    private List<LeagueStatusResponse> leaguesStatus;

    public LeagueGroupRelocationResponse() {
    }

    public LeagueGroupRelocationResponse(String leagueGroupName,
                                         Boolean isReadyToRelocate, Boolean isLeagueGroupFinished,
                                         List<LeagueStatusResponse> leaguesStatus) {
        this.leagueGroupName = leagueGroupName;
        this.isReadyToRelocate = isReadyToRelocate;
        this.isLeagueGroupFinished = isLeagueGroupFinished;
        this.leaguesStatus = leaguesStatus;
    }

    public String getLeagueGroupName() {
        return leagueGroupName;
    }

    public void setLeagueGroupName(String leagueGroupName) {
        this.leagueGroupName = leagueGroupName;
    }

    public Boolean getReadyToRelocate() {
        return isReadyToRelocate;
    }

    public void setReadyToRelocate(Boolean readyToRelocate) {
        isReadyToRelocate = readyToRelocate;
    }

    public Boolean getLeagueGroupFinished() {
        return isLeagueGroupFinished;
    }

    public void setLeagueGroupFinished(Boolean leagueGroupFinished) {
        isLeagueGroupFinished = leagueGroupFinished;
    }

    public List<LeagueStatusResponse> getLeaguesStatus() {
        return leaguesStatus;
    }

    public void setLeaguesStatus(List<LeagueStatusResponse> leaguesStatus) {
        this.leaguesStatus = leaguesStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueGroupRelocationResponse that = (LeagueGroupRelocationResponse) o;
        return Objects.equals(leagueGroupName, that.leagueGroupName) &&
                Objects.equals(isReadyToRelocate, that.isReadyToRelocate) &&
                Objects.equals(isLeagueGroupFinished, that.isLeagueGroupFinished) &&
                Objects.equals(leaguesStatus, that.leaguesStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueGroupName, isReadyToRelocate, isLeagueGroupFinished, leaguesStatus);
    }
}
