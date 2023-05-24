package aber.dcs.uk.shootingCompetitionsBackend.repositories;

import aber.dcs.uk.shootingCompetitionsBackend.dao.UserMemberDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);
    //Page<UserEntity> findAll(Pageable pageable);

    Page<UserEntity> findByEmailNot(String email, Pageable pageable);

    Optional<UserEntity> findByIdAndEmail(Long id, String email);

    @Query(value = "SELECT id AS userId, firstname, lastname FROM users " +
            "WHERE users.id = :userId", nativeQuery = true)
    Optional<UserMemberDao> findByIdSmallLimitedColumns(Long userId);

    @Query(value = "SELECT u.id AS userId, u.firstname AS firstname, u.lastname AS lastname " +
            "FROM users u " +
            "JOIN avg_shooting_scores ass ON u.id = ass.user_id "+
            "WHERE ass.gun_type = :gunType " +
            "ORDER BY firstname ASC",
            countQuery = "SELECT COUNT(*) FROM avg_shooting_scores ass " +
                    "WHERE ass.gun_type = :gunType",
            nativeQuery = true)
    Page<UserMemberDao> findByUsersWithGunTypeAverageScore(String gunType, Pageable pageable);
}
