package aber.dcs.uk.shootingCompetitionsBackend.entities.keys;

import java.util.Objects;

public class MatchSlotPointsKey {
    private Long competitorId;
    private Long shootingSlotId;

    public MatchSlotPointsKey() {
    }

    public MatchSlotPointsKey(Long competitorId, Long shootingSlotId) {
        this.competitorId = competitorId;
        this.shootingSlotId = shootingSlotId;
    }

    public Long getCompetitorId() {
        return competitorId;
    }

    public void setCompetitorId(Long competitorId) {
        this.competitorId = competitorId;
    }

    public Long getShootingSlotId() {
        return shootingSlotId;
    }

    public void setShootingSlotId(Long shootingSlotId) {
        this.shootingSlotId = shootingSlotId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchSlotPointsKey that = (MatchSlotPointsKey) o;
        return Objects.equals(competitorId, that.competitorId) &&
                Objects.equals(shootingSlotId, that.shootingSlotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(competitorId, shootingSlotId);
    }
}
