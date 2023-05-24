package aber.dcs.uk.shootingCompetitionsBackend.entities.keys;

import java.util.Objects;

public class LeagueCompetitorsKey {
    private Long competitorId;
    private Long leagueId;

    public LeagueCompetitorsKey() {
    }

    public LeagueCompetitorsKey(Long competitorId, Long leagueId) {
        this.competitorId = competitorId;
        this.leagueId = leagueId;
    }

    public Long getCompetitorId() {
        return competitorId;
    }

    public void setCompetitorId(Long competitorId) {
        this.competitorId = competitorId;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueCompetitorsKey that = (LeagueCompetitorsKey) o;
        return competitorId.equals(that.competitorId) && leagueId.equals(that.leagueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(competitorId, leagueId);
    }
}
