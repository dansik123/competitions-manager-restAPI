package aber.dcs.uk.shootingCompetitionsBackend.responses;


import java.util.List;
import java.util.Objects;

public class LeagueGenerateResponse {
    private String leagueName;
    private List<LeagueCompetitorRowTableResponse> leagueCompetitors;

    public LeagueGenerateResponse() {
    }

    public LeagueGenerateResponse(String leagueName,
                                  List<LeagueCompetitorRowTableResponse> leagueCompetitors) {
        this.leagueName = leagueName;
        this.leagueCompetitors = leagueCompetitors;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public List<LeagueCompetitorRowTableResponse> getLeagueCompetitors() {
        return leagueCompetitors;
    }

    public void setLeagueCompetitors(List<LeagueCompetitorRowTableResponse> leagueCompetitors) {
        this.leagueCompetitors = leagueCompetitors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueGenerateResponse that = (LeagueGenerateResponse) o;
        return leagueName.equals(that.leagueName) && leagueCompetitors.equals(that.leagueCompetitors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueName, leagueCompetitors);
    }
}
