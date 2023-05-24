package aber.dcs.uk.shootingCompetitionsBackend.repositories;

import aber.dcs.uk.shootingCompetitionsBackend.dao.RoundPauseUserDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingPauseSlotEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShootingPauseSlotRepository extends JpaRepository<ShootingPauseSlotEntity, String> {

    @Query(value = "SELECT sps.match_round_no AS roundNumber, u1.id AS user1Id, " +
            "u1.firstname AS user1Firstname, u1.lastname AS user1Lastname " +
            "FROM shooting_pause_slots sps " +
            "LEFT JOIN users u1 on sps.competitor_id = u1.id " +
            "WHERE sps.league_id = :leagueId AND league_round_no = :leagueCurrentRound " +
            "ORDER BY sps.match_round_no ASC", nativeQuery = true)
    List<RoundPauseUserDao> findAllUsersOnPauseForLeagueOrderByRoundNumberAsc(Long leagueId, Integer leagueCurrentRound);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM shooting_pause_slots sps where sps.league_id = :leagueId", nativeQuery = true)
    void deleteAllPauseSlotsByLeagueId(Long leagueId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM shooting_pause_slots sps where sps.league_id = :leagueId AND " +
            "sps.competitor_id = :userId", nativeQuery = true)
    void deleteAllPauseSlotsByLeagueIdAndUserId(Long leagueId, Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE shooting_pause_slots SET league_id = :newLeagueId " +
            "WHERE league_id = :oldLeagueId AND competitor_id = :user1Id", nativeQuery = true)
    void updateLeagueIdForOneUserAllPauseSlots(Long newLeagueId, Long oldLeagueId,  Long user1Id);
}
