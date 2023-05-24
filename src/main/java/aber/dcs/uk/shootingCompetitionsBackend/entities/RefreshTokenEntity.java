package aber.dcs.uk.shootingCompetitionsBackend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    @Id
    @Column(name = "refresh_token")
    private String refreshToken;
    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_REFRESH_TOKEN_LINKS_USER"))
    private UserEntity owner;

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(String refreshToken, UserEntity owner) {
        this.refreshToken = refreshToken;
        this.owner = owner;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }
}
