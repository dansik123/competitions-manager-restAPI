package aber.dcs.uk.shootingCompetitionsBackend.dao;

import java.math.BigDecimal;

public interface LeagueCompetitorDao extends UserMemberDao{
    BigDecimal getAvgScore();
}
