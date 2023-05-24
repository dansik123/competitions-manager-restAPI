package aber.dcs.uk.shootingCompetitionsBackend.dao;

public interface ShootingSlotScoresSummaryDao {
    Long getScoresOwnerId();
    Integer getScoresSum();
    Integer getScoresCount();
}
