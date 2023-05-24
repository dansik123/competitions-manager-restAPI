package aber.dcs.uk.shootingCompetitionsBackend.entities;

import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "avg_shooting_scores",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "gun_type"}) })
public class AverageShootingScoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="user_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_AVG_SHOOTING_SCORE_LINKS_USER"))
    private UserEntity user;

    @Column(name="avg_score")
    private BigDecimal averageScore;

    @Enumerated(EnumType.STRING)
    @Column(name="gun_type")
    private GunType gunType;

    public AverageShootingScoreEntity() {
    }

    public AverageShootingScoreEntity(
            Long id,
            UserEntity user,
            BigDecimal averageScore,
            GunType gunType) {
        this.id = id;
        this.user = user;
        this.averageScore = averageScore;
        this.gunType = gunType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public GunType getGunType() {
        return gunType;
    }

    public void setGunType(GunType gunType) {
        this.gunType = gunType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AverageShootingScoreEntity that = (AverageShootingScoreEntity) o;
        return id.equals(that.id) && user.equals(that.user) &&
                averageScore.equals(that.averageScore) && gunType == that.gunType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, averageScore, gunType);
    }
}
