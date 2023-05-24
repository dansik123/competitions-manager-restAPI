package aber.dcs.uk.shootingCompetitionsBackend.dao;

public interface SlotPointsDao {
    Long getSlotPointsOwnerId();
    Integer getSlotPoints();
    String getSlotShootingScore();
    Long getSlotMatchCompetitor1Id();
    Long getSlotMatchCompetitor2Id();
}
