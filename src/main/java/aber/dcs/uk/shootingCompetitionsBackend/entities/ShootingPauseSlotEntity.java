package aber.dcs.uk.shootingCompetitionsBackend.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "shooting_pause_slots")
public class ShootingPauseSlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id",
            foreignKey = @ForeignKey(name = "FK_SHOOTING_PAUSE_SLOT_LINKS_LEAGUE"))
    private LeagueEntity league;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_id",
            foreignKey = @ForeignKey(name = "FK_SHOOTING_PAUSE_SLOT_COMPETITOR_1_LINKS_USER"))
    private UserEntity competitor;

    @Column(name= "league_round_no")
    private Integer leagueRoundNumber;

    @Column(name= "match_round_no")
    private Integer matchRoundNumber;

    public ShootingPauseSlotEntity() {
    }

    public ShootingPauseSlotEntity(Long id, LeagueEntity league,
                                   UserEntity competitor, Integer leagueRoundNumber,
                                   Integer matchRoundNumber) {
        this.id = id;
        this.league = league;
        this.competitor = competitor;
        this.leagueRoundNumber = leagueRoundNumber;
        this.matchRoundNumber = matchRoundNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LeagueEntity getLeague() {
        return league;
    }

    public void setLeague(LeagueEntity league) {
        this.league = league;
    }

    public UserEntity getCompetitor() {
        return competitor;
    }

    public void setCompetitor(UserEntity competitor) {
        this.competitor = competitor;
    }

    public Integer getLeagueRoundNumber() {
        return leagueRoundNumber;
    }

    public void setLeagueRoundNumber(Integer leagueRoundNumber) {
        this.leagueRoundNumber = leagueRoundNumber;
    }

    public Integer getMatchRoundNumber() {
        return matchRoundNumber;
    }

    public void setMatchRoundNumber(Integer matchRoundNumber) {
        this.matchRoundNumber = matchRoundNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShootingPauseSlotEntity that = (ShootingPauseSlotEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(league, that.league) &&
                Objects.equals(competitor, that.competitor) &&
                Objects.equals(leagueRoundNumber, that.leagueRoundNumber) &&
                Objects.equals(matchRoundNumber, that.matchRoundNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, league, competitor, leagueRoundNumber, matchRoundNumber);
    }
}
