package aber.dcs.uk.shootingCompetitionsBackend.repositories;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ShootingSlotDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.ShootingSlotScoresSummaryDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShootingSlotRepository extends JpaRepository<ShootingSlotEntity, Long> {

//ALTERNATIVE FOR SELECTING SHOOTING SLOT WITH FULL COMPETITOR DETAILS FROM USER TABLE
//    @Query("SELECT slot FROM ShootingSlotEntity slot " +
//        "JOIN FETCH slot.competitor1 "+
//        "JOIN FETCH slot.competitor2 " +
//        "WHERE slot.hasOpponent = :hasOpponent AND slot.league = :league "+
//        "ORDER BY slot.roundNumber ASC")
//    List<ShootingSlotEntity> findAllByLeagueAndHasOpponentOrderByRoundNumberAsc(LeagueEntity league, Boolean hasOpponent);

    @Query(value = "SELECT ss.id AS id, ss.match_round_no AS roundNumber, ss.slot_date AS slotDate, " +
            "u1.id AS user1Id, u1.firstname AS user1Firstname, u1.lastname AS user1Lastname, " +
            "u2.id AS user2Id, u2.firstname AS user2Firstname, u2.lastname AS user2Lastname, " +
            "ss.has_score_result as hasScoreResult, ss.competitor_1_score as competitor1Score, " +
            "ss.competitor_2_score as competitor2Score, " +
            "ss.competitor_1_score_card_link as competitor1ScoreCardLink, " +
            "ss.competitor_2_score_card_link as competitor2ScoreCardLink " +
            "FROM shooting_slots ss " +
            "LEFT JOIN users u1 on ss.competitor_1_id = u1.id " +
            "LEFT JOIN users u2 on ss.competitor_2_id = u2.id " +
            "WHERE league_id = :leagueId AND league_round_no = :leagueCurrentRound " +
            "ORDER BY roundNumber ASC", nativeQuery = true)
    List<ShootingSlotDao> findAllByLeagueAndHasOpponentOrderByRoundNumberAsc(
            Long leagueId, Integer leagueCurrentRound);

    @Query(value = "SELECT ss.id AS id, ss.match_round_no AS roundNumber, ss.slot_date AS slotDate, " +
            "u1.id AS user1Id, u1.firstname AS user1Firstname, u1.lastname AS user1Lastname, " +
            "u2.id AS user2Id, u2.firstname AS user2Firstname, u2.lastname AS user2Lastname, " +
            "ss.has_score_result as hasScoreResult, ss.competitor_1_score as competitor1Score, " +
            "ss.competitor_2_score as competitor2Score, " +
            "ss.competitor_1_score_card_link as competitor1ScoreCardLink, " +
            "ss.competitor_2_score_card_link as competitor2ScoreCardLink " +
            "FROM shooting_slots ss " +
            "LEFT JOIN users u1 on ss.competitor_1_id = u1.id " +
            "LEFT JOIN users u2 on ss.competitor_2_id = u2.id " +
            "WHERE ss.id = :matchId", nativeQuery = true)
    Optional<ShootingSlotDao> findSingleMatchSlotById(Long matchId);

    @Query(value = "SELECT ss.id AS id, ss.match_round_no AS roundNumber, ss.slot_date AS slotDate, " +
            "u1.id AS user1Id, u1.firstname AS user1Firstname, u1.lastname AS user1Lastname, " +
            "u2.id AS user2Id, u2.firstname AS user2Firstname, u2.lastname AS user2Lastname, " +
            "ss.has_score_result as hasScoreResult, ss.competitor_1_score as competitor1Score, " +
            "ss.competitor_2_score as competitor2Score, " +
            "ss.competitor_1_score_card_link as competitor1ScoreCardLink, " +
            "ss.competitor_2_score_card_link as competitor2ScoreCardLink " +
            "FROM shooting_slots ss " +
            "LEFT JOIN users u1 on ss.competitor_1_id = u1.id " +
            "LEFT JOIN users u2 on ss.competitor_2_id = u2.id " +
            "WHERE ss.league_id = :leagueId " +
            "AND (ss.competitor_1_id = :userId OR ss.competitor_2_id = :userId) " +
            "ORDER BY league_round_no ASC, roundNumber ASC", nativeQuery = true)
    List<ShootingSlotDao> findAllMatchesWithUserIdAndHasOpponent(Long leagueId, Long userId);

    @Query(value="SELECT competitors.userId AS scoresOwnerId, " +
            "SUM(case when competitor_1_id = competitors.userId THEN " +
            "competitor_1_score ELSE competitor_2_score END) as scoresSum, " +
            "COUNT(competitors.userId) scoresCount " +
            "FROM shooting_slots ss " +
            "JOIN (SELECT lc.competitor_id AS userId FROM league_competitors lc " +
            "WHERE lc.league_id IN :leaguesIds) competitors ON " +
            "competitors.userId = ss.competitor_1_id OR competitors.userId = ss.competitor_2_id " +
            "WHERE ss.league_id IN :leaguesIds " +
            "GROUP BY competitors.userId", nativeQuery = true)
    List<ShootingSlotScoresSummaryDao> getScoresSumAndCountPerCompetitorInLeagueGroup(List<Long> leaguesIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM shooting_slots ss where ss.league_id = :leagueId", nativeQuery = true)
    void deleteAllSlotsByLeagueId(Long leagueId);

    List<ShootingSlotEntity> findByLeagueAndCompetitor1(
            LeagueEntity league, UserEntity competitor1);

    List<ShootingSlotEntity> findByLeagueAndCompetitor2(
            LeagueEntity league, UserEntity competitor2);

    @Query(value="SELECT ss.competitor_1_score_card_link " +
            "FROM shooting_slots ss WHERE ss.id = :matchId",
            nativeQuery = true)
    Optional<String> findCompetitor1MatchImage(Long matchId);

    @Query(value="SELECT ss.competitor_2_score_card_link " +
            "FROM shooting_slots ss WHERE ss.id = :matchId",
            nativeQuery = true)
    Optional<String> findCompetitor2MatchImage(Long matchId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE shooting_slots SET league_id = :newLeagueId " +
            "WHERE league_id = :oldLeagueId AND (competitor_1_id = :user1Id OR competitor_2_id = :user1Id)", nativeQuery = true)
    void updateLeagueIdForOneUserAllMatchSlots(Long newLeagueId, Long oldLeagueId, Long user1Id);

    @Query(value="SELECT ss.league_id " +
            "FROM shooting_slots ss " +
            "WHERE ss.league_id in :leaguesIds AND ss.has_score_result = false " +
            "GROUP BY ss.league_id ", nativeQuery = true)
    List<Long> getLeaguesIdsByUnfinishedSlots(List<Long> leaguesIds);
}
