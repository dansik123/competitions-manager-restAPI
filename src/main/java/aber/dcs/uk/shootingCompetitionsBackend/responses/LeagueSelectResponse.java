package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.dao.LeagueSelectDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;

import java.util.Objects;

public class LeagueSelectResponse implements LeagueSelectDao {
    private Long leagueId;
    private String leagueName;

    public LeagueSelectResponse() {
    }

    public LeagueSelectResponse(Long leagueId, String leagueName) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
    }

    public LeagueSelectResponse(LeagueSelectDao leagueSelectDao){
        this.leagueId = leagueSelectDao.getLeagueId();
        this.leagueName = leagueSelectDao.getLeagueName();
    }

    public LeagueSelectResponse(LeagueEntity leagueEntity) {
        this.leagueId = leagueEntity.getId();
        this.leagueName = leagueEntity.getName();
    }

    @Override
    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    @Override
    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueSelectResponse that = (LeagueSelectResponse) o;
        return leagueId.equals(that.leagueId) && leagueName.equals(that.leagueName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueId, leagueName);
    }
}
