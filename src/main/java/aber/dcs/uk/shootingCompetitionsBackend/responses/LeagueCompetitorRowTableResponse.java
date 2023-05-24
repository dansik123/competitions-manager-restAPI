package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.dao.LeagueCompetitorDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.LeagueCompetitorGenerateGroupsDao;

import java.math.BigDecimal;
import java.util.Objects;

public class LeagueCompetitorRowTableResponse implements LeagueCompetitorDao {
    private Long userId;
    private String firstname;
    private String lastname;
    private BigDecimal avgScore;

    public LeagueCompetitorRowTableResponse() {
    }

    public LeagueCompetitorRowTableResponse(
            Long userId, String firstname, String lastname, BigDecimal avgScore) {
        this.userId = userId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.avgScore = avgScore;
    }

    public LeagueCompetitorRowTableResponse(LeagueCompetitorGenerateGroupsDao leagueCompetitor){
        this.userId = leagueCompetitor.getUserId();
        this.firstname = leagueCompetitor.getFirstname();
        this.lastname = leagueCompetitor.getLastname();
        this.avgScore = leagueCompetitor.getAvgScore();
    }

    public LeagueCompetitorRowTableResponse(LeagueCompetitorDao leagueCompetitorDao){
        this.userId = leagueCompetitorDao.getUserId();
        this.firstname = leagueCompetitorDao.getFirstname();
        this.lastname = leagueCompetitorDao.getLastname();
        this.avgScore = leagueCompetitorDao.getAvgScore();
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public String getFirstname() {
        return firstname;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    @Override
    public BigDecimal getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(BigDecimal avgScore) {
        this.avgScore = avgScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueCompetitorRowTableResponse that = (LeagueCompetitorRowTableResponse) o;
        return userId.equals(that.userId) && Objects.equals(firstname, that.firstname) &&
                Objects.equals(lastname, that.lastname) && Objects.equals(avgScore, that.avgScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstname, lastname, avgScore);
    }
}
