package aber.dcs.uk.shootingCompetitionsBackend.dao;

public interface LeagueDao {
    Long getLeagueId();
    String getLeagueName();
    Integer getLeagueMaxCompetitors();
    Integer getCompetitorsCount();
    Integer getTotalRounds();
    Integer getCurrentRoundNo();
    Boolean getMatchesGenerated();
    String getLeagueGunType();
}
