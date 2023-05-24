package aber.dcs.uk.shootingCompetitionsBackend.repositories;

import aber.dcs.uk.shootingCompetitionsBackend.entities.RefreshTokenEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM RefreshTokenEntity entity WHERE entity.owner= :user")
    void removeAllUserRefreshTokens(@Param("user") UserEntity user);
}
