package aber.dcs.uk.shootingCompetitionsBackend.entities;

import aber.dcs.uk.shootingCompetitionsBackend.entities.keys.LeagueCompetitorsKey;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "league_competitors")
@IdClass(LeagueCompetitorsKey.class)
public class LeagueCompetitorsEntity {
    @Id
    @Column(name = "competitor_id")
    private Long competitorId;

    @Id
    @Column(name = "league_id")
    private Long leagueId;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "competitor_id",
            foreignKey = @ForeignKey(name = "FK_LEAGUE_COMPETITOR_LINKS_USER"))
    private UserEntity competitor;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "league_id",
            foreignKey = @ForeignKey(name = "FK_LEAGUE_COMPETITOR_LINKS_LEAGUE"))
    private LeagueEntity league;

    public LeagueCompetitorsEntity() {
    }

    public LeagueCompetitorsEntity(Long competitorId, Long leagueId) {
        this.competitorId = competitorId;
        this.leagueId = leagueId;
    }

    public LeagueCompetitorsEntity(Long competitorId, Long leagueId, UserEntity competitor, LeagueEntity league) {
        this.competitorId = competitorId;
        this.leagueId = leagueId;
        this.competitor = competitor;
        this.league = league;
    }

    public Long getCompetitorId() {
        return competitorId;
    }

    public void setCompetitorId(Long competitorId) {
        this.competitorId = competitorId;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    public UserEntity getCompetitor() {
        return competitor;
    }

    public void setCompetitor(UserEntity competitor) {
        this.competitor = competitor;
    }

    public LeagueEntity getLeague() {
        return league;
    }

    public void setLeague(LeagueEntity league) {
        this.league = league;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueCompetitorsEntity that = (LeagueCompetitorsEntity) o;
        return competitorId.equals(that.competitorId) && leagueId.equals(that.leagueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(competitorId, leagueId);
    }
}
