package aber.dcs.uk.shootingCompetitionsBackend.repositories;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ScoreDetailsDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.AverageShootingScoreEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AverageShootingScoreRepository extends JpaRepository<AverageShootingScoreEntity, Long> {
    @Query(value = "SELECT avg_score AS avgScore, gun_type AS gunType " +
            "FROM avg_shooting_scores " +
            "WHERE user_id = :userId AND gun_type = :gunType", nativeQuery = true)
    Optional<ScoreDetailsDao> findScoreDetailByUserIdAndGunType(Long userId, String gunType);

    @Query(value = "SELECT avg_score AS avgScore, gun_type AS gunType FROM avg_shooting_scores " +
            "WHERE user_id = :userId", nativeQuery = true)
    List<ScoreDetailsDao> findAllScoreDetailsByUserId(Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE avg_shooting_scores set avg_score = :newAvgScore " +
            "WHERE user_id = :userId AND gun_type = :gunType", nativeQuery = true)
    void updateUserAverageScoreByUserIdAndGunType(Long userId, String gunType, BigDecimal newAvgScore);
}
