package aber.dcs.uk.shootingCompetitionsBackend.entities;

import aber.dcs.uk.shootingCompetitionsBackend.entities.keys.MatchSlotPointsKey;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "shooting_slots_points")
@IdClass(MatchSlotPointsKey.class)
public class MatchSlotPointsEntity {
    @Id
    @Column(name = "competitor_id")
    private Long competitorId;

    @Id
    @Column(name = "shooting_slot_id")
    private Long shootingSlotId;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_id",
            foreignKey = @ForeignKey(name = "FK_SHOOTING_POINTS_MATCH_SLOT_LINKS_USER"))
    private UserEntity competitor;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shooting_slot_id",
            foreignKey = @ForeignKey(name = "FK_SHOOTING_POINTS_MATCH_SLOT_LINKS_SHOOTING_SLOT"))
    private ShootingSlotEntity shootingSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id",
            foreignKey = @ForeignKey(name = "FK_SHOOTING_POINTS_MATCH_SLOT_LINKS_LEAGUE"))
    private LeagueEntity league;

    @Column(name="league_match_points")
    private Integer points;

    public MatchSlotPointsEntity() {
    }

    public MatchSlotPointsEntity(Long competitorId, Long shootingSlotId, UserEntity competitor,
                                 ShootingSlotEntity shootingSlot, LeagueEntity league, Integer points) {
        this.competitorId = competitorId;
        this.shootingSlotId = shootingSlotId;
        this.competitor = competitor;
        this.shootingSlot = shootingSlot;
        this.league = league;
        this.points = points;
    }

    public MatchSlotPointsEntity(Long competitorId, Long shootingSlotId, LeagueEntity league, Integer points) {
        this.competitorId = competitorId;
        this.shootingSlotId = shootingSlotId;
        this.league = league;
        this.points = points;
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

    public UserEntity getCompetitor() {
        return competitor;
    }

    public void setCompetitor(UserEntity competitor) {
        this.competitor = competitor;
    }

    public ShootingSlotEntity getShootingSlot() {
        return shootingSlot;
    }

    public void setShootingSlot(ShootingSlotEntity shootingSlot) {
        this.shootingSlot = shootingSlot;
    }

    public LeagueEntity getLeague() {
        return league;
    }

    public void setLeague(LeagueEntity league) {
        this.league = league;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchSlotPointsEntity that = (MatchSlotPointsEntity) o;
        return Objects.equals(competitorId, that.competitorId) &&
                Objects.equals(shootingSlotId, that.shootingSlotId) &&
                Objects.equals(competitor, that.competitor) &&
                Objects.equals(shootingSlot, that.shootingSlot) &&
                Objects.equals(league, that.league) && Objects.equals(points, that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(competitorId, shootingSlotId, competitor, shootingSlot, league, points);
    }
}
