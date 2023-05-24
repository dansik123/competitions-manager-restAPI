package aber.dcs.uk.shootingCompetitionsBackend.dao;


import java.math.BigDecimal;

public interface ScoreDetailsDao {
    String getGunType();

    BigDecimal getAvgScore();
}
