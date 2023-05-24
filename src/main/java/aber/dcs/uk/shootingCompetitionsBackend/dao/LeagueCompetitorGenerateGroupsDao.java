package aber.dcs.uk.shootingCompetitionsBackend.dao;

public interface LeagueCompetitorGenerateGroupsDao extends UserMemberDao, ScoreDetailsDao{
    Integer getGroupId();
}
