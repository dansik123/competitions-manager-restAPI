package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ScoreDetailsDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.AverageShootingScoreEntity;

import java.math.BigDecimal;
import java.util.Objects;

public class ScoreDetailsResponse implements ScoreDetailsDao{
    private String gunType;
    private BigDecimal avgScore;

    public ScoreDetailsResponse() {
    }

    public ScoreDetailsResponse(String gunType, BigDecimal avgScore) {
        this.gunType = gunType;
        this.avgScore = avgScore;
    }

    public ScoreDetailsResponse(AverageShootingScoreEntity averageShootingScore) {
        this.gunType = averageShootingScore.getGunType().name();
        this.avgScore = averageShootingScore.getAverageScore();
    }

    public ScoreDetailsResponse(ScoreDetailsDao scoreDetailsDao) {
        this.gunType = scoreDetailsDao.getGunType();
        this.avgScore = scoreDetailsDao.getAvgScore();
    }

    public String getGunType() {
        return gunType;
    }

    public void setGunType(String gunType) {
        this.gunType = gunType;
    }

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
        ScoreDetailsResponse that = (ScoreDetailsResponse) o;
        return Objects.equals(gunType, that.gunType) && avgScore.equals(that.avgScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gunType, avgScore);
    }
}
