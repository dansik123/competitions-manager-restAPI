package aber.dcs.uk.shootingCompetitionsBackend.entities;

import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "leagues")
public class LeagueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "league_name")
    private String name;
    @Column(name="max_competitors")
    private Integer maxCompetitors;
    @Column(name="competitors_count")
    private Integer numberOfCompetitors;
    @Column(name="expected_rounds_to_play")
    private Integer totalRoundsToPlay;
    @Column(name="current_round_no")
    private Integer currentRound;

    @Column(name="league_matches_generated")
    private Boolean hasGeneratedMatches;

    @Column(name="gun_type")
    @Enumerated(EnumType.STRING)
    private GunType gunType;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="league", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeagueCompetitorsEntity> leagueCompetitors;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="league", orphanRemoval = true)
    private List<ShootingSlotEntity> shootingSlots;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="league", orphanRemoval = true)
    private List<ShootingPauseSlotEntity> shootingPauseSlots;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="league", orphanRemoval = true)
    private List<MatchSlotPointsEntity> matchPointsList;

    public LeagueEntity() {
        leagueCompetitors = new ArrayList<>();
        shootingSlots = new ArrayList<>();
        shootingPauseSlots = new ArrayList<>();
        matchPointsList = new ArrayList<>();
    }

    public LeagueEntity(Long id) {
        this.id = id;
    }

    public LeagueEntity(Long id, String name, Integer maxCompetitors,
                        Integer numberOfCompetitors, Integer totalRoundsToPlay,
                        Integer currentRound, Boolean hasGeneratedMatches, GunType gunType,
                        List<LeagueCompetitorsEntity> leagueCompetitors,
                        List<ShootingSlotEntity> shootingSlots,
                        List<ShootingPauseSlotEntity> shootingPauseSlots) {
        this.id = id;
        this.name = name;
        this.maxCompetitors = maxCompetitors;
        this.numberOfCompetitors = numberOfCompetitors;
        this.totalRoundsToPlay = totalRoundsToPlay;
        this.currentRound = currentRound;
        this.hasGeneratedMatches = hasGeneratedMatches;
        this.gunType = gunType;
        this.leagueCompetitors = leagueCompetitors;
        this.shootingSlots = shootingSlots;
        this.shootingPauseSlots = shootingPauseSlots;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxCompetitors() {
        return maxCompetitors;
    }

    public void setMaxCompetitors(Integer maxCompetitors) {
        this.maxCompetitors = maxCompetitors;
    }

    public Integer getNumberOfCompetitors() {
        return numberOfCompetitors;
    }

    public void setNumberOfCompetitors(Integer numberOfCompetitors) {
        this.numberOfCompetitors = numberOfCompetitors;
    }

    public Integer getTotalRoundsToPlay() {
        return totalRoundsToPlay;
    }

    public void setTotalRoundsToPlay(Integer totalRoundsToPlay) {
        this.totalRoundsToPlay = totalRoundsToPlay;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public Boolean getHasGeneratedMatches() {
        return hasGeneratedMatches;
    }

    public void setHasGeneratedMatches(Boolean hasGeneratedMatches) {
        this.hasGeneratedMatches = hasGeneratedMatches;
    }

    public GunType getGunType() {
        return gunType;
    }

    public void setGunType(GunType gunType) {
        this.gunType = gunType;
    }

    public List<LeagueCompetitorsEntity> getLeagueCompetitors() {
        return leagueCompetitors;
    }

    public void setLeagueCompetitors(List<LeagueCompetitorsEntity> leagueCompetitors) {
        this.leagueCompetitors = leagueCompetitors;
    }

    public List<ShootingSlotEntity> getShootingSlots() {
        return shootingSlots;
    }

    public void setShootingSlots(List<ShootingSlotEntity> shootingSlots) {
        this.shootingSlots = shootingSlots;
    }

    public List<ShootingPauseSlotEntity> getShootingPauseSlots() {
        return shootingPauseSlots;
    }

    public void setShootingPauseSlots(List<ShootingPauseSlotEntity> shootingPauseSlots) {
        this.shootingPauseSlots = shootingPauseSlots;
    }

    public List<MatchSlotPointsEntity> getMatchPointsList() {
        return matchPointsList;
    }

    public void setMatchPointsList(List<MatchSlotPointsEntity> matchPointsList) {
        this.matchPointsList = matchPointsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueEntity that = (LeagueEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) &&
                Objects.equals(maxCompetitors, that.maxCompetitors) &&
                Objects.equals(numberOfCompetitors, that.numberOfCompetitors) &&
                Objects.equals(totalRoundsToPlay, that.totalRoundsToPlay) &&
                Objects.equals(currentRound, that.currentRound) &&
                Objects.equals(hasGeneratedMatches, that.hasGeneratedMatches) &&
                gunType == that.gunType && Objects.equals(leagueCompetitors, that.leagueCompetitors) &&
                Objects.equals(shootingSlots, that.shootingSlots) &&
                Objects.equals(shootingPauseSlots, that.shootingPauseSlots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, maxCompetitors, numberOfCompetitors, totalRoundsToPlay,
                currentRound, hasGeneratedMatches, gunType, leagueCompetitors, shootingSlots, shootingPauseSlots);
    }
}
