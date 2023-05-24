package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.dao.LeagueDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;

import java.util.Objects;

public class UserLeagueResponse implements LeagueDao {
    private Long leagueId;
    private String leagueName;
    private Integer leagueMaxCompetitors;
    private Integer competitorsCount;
    private Integer totalRounds;
    private Integer currentRoundNo;
    private Boolean matchesGenerated;
    private String leagueGunType;

    public UserLeagueResponse() {
    }

    public UserLeagueResponse(Long leagueId, String leagueName, Integer leagueMaxCompetitors,
                              Integer competitorsCount, Integer totalRounds,
                              Integer currentRoundNo, Boolean matchesGenerated,
                              String leagueGunType) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.leagueMaxCompetitors = leagueMaxCompetitors;
        this.competitorsCount = competitorsCount;
        this.totalRounds = totalRounds;
        this.currentRoundNo = currentRoundNo;
        this.matchesGenerated = matchesGenerated;
        this.leagueGunType = leagueGunType;
    }

    public UserLeagueResponse(LeagueDao leagueDao) {
        this.leagueId = leagueDao.getLeagueId();
        this.leagueName = leagueDao.getLeagueName();
        this.leagueMaxCompetitors = leagueDao.getLeagueMaxCompetitors();
        this.competitorsCount = leagueDao.getCompetitorsCount();
        this.totalRounds = leagueDao.getTotalRounds();
        this.currentRoundNo = leagueDao.getCurrentRoundNo();
        this.matchesGenerated = leagueDao.getMatchesGenerated();
        this.leagueGunType = leagueDao.getLeagueGunType();
    }

    public UserLeagueResponse(LeagueEntity leagueEntity) {
        this.leagueId = leagueEntity.getId();
        this.leagueName = leagueEntity.getName();
        this.leagueMaxCompetitors = leagueEntity.getMaxCompetitors();
        this.competitorsCount = leagueEntity.getNumberOfCompetitors();
        this.totalRounds = leagueEntity.getTotalRoundsToPlay();
        this.currentRoundNo = leagueEntity.getCurrentRound();
        this.matchesGenerated = leagueEntity.getHasGeneratedMatches();
        this.leagueGunType = leagueEntity.getGunType().name();
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
    public Integer getLeagueMaxCompetitors() {
        return leagueMaxCompetitors;
    }

    public void setLeagueMaxCompetitors(Integer leagueMaxCompetitors) {
        this.leagueMaxCompetitors = leagueMaxCompetitors;
    }

    @Override
    public Integer getCompetitorsCount() {
        return competitorsCount;
    }

    public void setCompetitorsCount(Integer competitorsCount) {
        this.competitorsCount = competitorsCount;
    }

    @Override
    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    @Override
    public Integer getCurrentRoundNo() {
        return currentRoundNo;
    }

    public void setCurrentRoundNo(Integer currentRoundNo) {
        this.currentRoundNo = currentRoundNo;
    }

    @Override
    public Boolean getMatchesGenerated() {
        return matchesGenerated;
    }

    public void setMatchesGenerated(Boolean matchesGenerated) {
        this.matchesGenerated = matchesGenerated;
    }

    @Override
    public String getLeagueGunType() {
        return leagueGunType;
    }

    public void setLeagueGunType(String leagueGunType) {
        this.leagueGunType = leagueGunType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLeagueResponse that = (UserLeagueResponse) o;
        return Objects.equals(leagueId, that.leagueId) &&
                Objects.equals(leagueName, that.leagueName) &&
                Objects.equals(leagueMaxCompetitors, that.leagueMaxCompetitors) &&
                Objects.equals(competitorsCount, that.competitorsCount) &&
                Objects.equals(totalRounds, that.totalRounds) &&
                Objects.equals(currentRoundNo, that.currentRoundNo) &&
                Objects.equals(matchesGenerated, that.matchesGenerated) &&
                Objects.equals(leagueGunType, that.leagueGunType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueId, leagueName, leagueMaxCompetitors, competitorsCount,
                totalRounds, currentRoundNo, matchesGenerated, leagueGunType);
    }
}
