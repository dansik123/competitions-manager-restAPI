package aber.dcs.uk.shootingCompetitionsBackend.entities;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "shooting_slots",
        uniqueConstraints = { @UniqueConstraint( name="UNIQUE_SHOOTING_SLOT",
            columnNames = {
                    "league_id", "competitor_1_id", "competitor_2_id", "league_round_no"}) })
public class ShootingSlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id",
            foreignKey = @ForeignKey(name = "FK_SHOOTING_SLOT_LINKS_LEAGUE"))
    private LeagueEntity league;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="shootingSlot", orphanRemoval = true)
    private List<MatchSlotPointsEntity> matchSlotPoints;

    @ManyToOne
    @JoinColumn(name = "competitor_1_id",
            foreignKey = @ForeignKey(name = "FK_SHOOTING_SLOT_COMPETITOR_1_LINKS_USER"))
    private UserEntity competitor1;

    @ManyToOne
    @JoinColumn(name = "competitor_2_id",
            foreignKey = @ForeignKey(name = "FK_SHOOTING_SLOT_COMPETITOR_2_LINKS_USER"))
    private UserEntity competitor2;

    @Column(name = "slot_date")
    @Temporal(TemporalType.DATE)
    private Date slotDate;

    @Column(name= "league_round_no")
    private Integer leagueRoundNumber;

    @Column(name= "match_round_no")
    private Integer matchRoundNumber;
    @Column(name="has_score_result")
    private Boolean hasScoreResult;

    @Column(name = "competitor_1_score")
    private Integer competitor1Score;

    @Column(name = "competitor_2_score")
    private Integer competitor2Score;

    @Column(name="competitor_1_score_card_link")
    private String competitor1ScoreCardLink;
    @Column(name="competitor_2_score_card_link")
    private String competitor2ScoreCardLink;

    public ShootingSlotEntity() {
    }

    public ShootingSlotEntity(Long id, LeagueEntity league,
                              UserEntity competitor1, UserEntity competitor2,
                              Date slotDate, Integer leagueRoundNumber,
                              Integer matchRoundNumber, Boolean hasScoreResult,
                              Integer competitor1Score, Integer competitor2Score,
                              String competitor1ScoreCardLink, String competitor2ScoreCardLink) {
        this.id = id;
        this.league = league;
        this.competitor1 = competitor1;
        this.competitor2 = competitor2;
        this.slotDate = slotDate;
        this.leagueRoundNumber = leagueRoundNumber;
        this.matchRoundNumber = matchRoundNumber;
        this.hasScoreResult = hasScoreResult;
        this.competitor1Score = competitor1Score;
        this.competitor2Score = competitor2Score;
        this.competitor1ScoreCardLink = competitor1ScoreCardLink;
        this.competitor2ScoreCardLink = competitor2ScoreCardLink;
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

    public List<MatchSlotPointsEntity> getMatchSlotPoints() {
        return matchSlotPoints;
    }

    public void setMatchSlotPoints(List<MatchSlotPointsEntity> matchSlotPoints) {
        this.matchSlotPoints = matchSlotPoints;
    }

    public UserEntity getCompetitor1() {
        return competitor1;
    }

    public void setCompetitor1(UserEntity competitor1) {
        this.competitor1 = competitor1;
    }

    public UserEntity getCompetitor2() {
        return competitor2;
    }

    public void setCompetitor2(UserEntity competitor2) {
        this.competitor2 = competitor2;
    }

    public Date getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(Date slotDate) {
        this.slotDate = slotDate;
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

    public Boolean getHasScoreResult() {
        return hasScoreResult;
    }

    public void setHasScoreResult(Boolean hasScoreResult) {
        this.hasScoreResult = hasScoreResult;
    }

    public Integer getCompetitor1Score() {
        return competitor1Score;
    }

    public void setCompetitor1Score(Integer competitor1Score) {
        this.competitor1Score = competitor1Score;
    }

    public Integer getCompetitor2Score() {
        return competitor2Score;
    }

    public void setCompetitor2Score(Integer competitor2Score) {
        this.competitor2Score = competitor2Score;
    }

    public String getCompetitor1ScoreCardLink() {
        return competitor1ScoreCardLink;
    }

    public void setCompetitor1ScoreCardLink(String competitor1ScoreCardLink) {
        this.competitor1ScoreCardLink = competitor1ScoreCardLink;
    }

    public String getCompetitor2ScoreCardLink() {
        return competitor2ScoreCardLink;
    }

    public void setCompetitor2ScoreCardLink(String competitor2ScoreCardLink) {
        this.competitor2ScoreCardLink = competitor2ScoreCardLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShootingSlotEntity that = (ShootingSlotEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(league, that.league) &&
                Objects.equals(matchSlotPoints, that.matchSlotPoints) &&
                Objects.equals(competitor1, that.competitor1) &&
                Objects.equals(competitor2, that.competitor2) &&
                Objects.equals(slotDate, that.slotDate) &&
                Objects.equals(leagueRoundNumber, that.leagueRoundNumber) &&
                Objects.equals(matchRoundNumber, that.matchRoundNumber) &&
                Objects.equals(hasScoreResult, that.hasScoreResult) &&
                Objects.equals(competitor1Score, that.competitor1Score) &&
                Objects.equals(competitor2Score, that.competitor2Score) &&
                Objects.equals(competitor1ScoreCardLink, that.competitor1ScoreCardLink) &&
                Objects.equals(competitor2ScoreCardLink, that.competitor2ScoreCardLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, league, matchSlotPoints, competitor1, competitor2, slotDate,
                leagueRoundNumber, matchRoundNumber, hasScoreResult, competitor1Score,
                competitor2Score, competitor1ScoreCardLink, competitor2ScoreCardLink);
    }
}
