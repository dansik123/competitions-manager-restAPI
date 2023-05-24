package aber.dcs.uk.shootingCompetitionsBackend.repositories;

import aber.dcs.uk.shootingCompetitionsBackend.dao.AllLeaguesPointsDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.LeaguePointsSummaryDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.LeagueRelocateUsersDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.LeagueSlotPointsDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.MatchSlotPointsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchSlotPointsRepository extends JpaRepository<MatchSlotPointsEntity, Long> {
    @Modifying
    @Query(value = "UPDATE shooting_slots_points SET league_match_points = :points " +
            "WHERE shooting_slot_id = :slotId AND competitor_id = :userId", nativeQuery = true)
    void updateLeagueMatchSlotPoints(Long userId, Long slotId, Integer points);

    //In query I added order by with 2 columns usually the slots are created in way where the following slot
    // have the same of increased by one round id
    //If data gets generated in different way the second column in ORDER BY statement makes sure that
    //I have data always in right order
    @Query(value="SELECT ssp.competitor_id AS slotPointsOwnerId, ssp.league_match_points AS slotPoints, " +
            "ss.match_round_no AS roundNumber, " +
            "ss.competitor_1_id AS slotMatchCompetitor1Id, ss.competitor_2_id AS slotMatchCompetitor2Id, " +
            "CASE WHEN ss.has_score_result THEN CONCAT(ss.competitor_1_score, '-', ss.competitor_2_score) " +
            "ELSE NULL END AS slotShootingScore " +
            "FROM shooting_slots_points ssp " +
            "JOIN shooting_slots ss ON ssp.shooting_slot_id = ss.id " +
            "WHERE ssp.league_id = :leagueId AND ss.league_round_no = :leagueCurrentRound " +
            "ORDER BY ssp.shooting_slot_id, ss.match_round_no",
        nativeQuery = true)
    List<LeagueSlotPointsDao> getAllPointsWitIdsUsersForCurrentLeagueRound(Long leagueId, Integer leagueCurrentRound);

    @Query(value="SELECT ssp.competitor_id AS slotPointsOwnerId, ssp.league_match_points AS slotPoints, " +
            "ss.league_round_no AS leagueRoundNumber, " +
            "ss.competitor_1_id AS slotMatchCompetitor1Id, ss.competitor_2_id AS slotMatchCompetitor2Id,  " +
            "CASE WHEN ss.has_score_result THEN CONCAT(ss.competitor_1_score, '-', ss.competitor_2_score)  " +
            "ELSE NULL END AS slotShootingScore  " +
            "FROM shooting_slots_points ssp  " +
            "JOIN shooting_slots ss ON ssp.shooting_slot_id = ss.id  " +
            "WHERE ssp.league_id IN :leaguesIds " +
            "ORDER BY ssp.shooting_slot_id, ss.match_round_no",
            nativeQuery = true)
    List<AllLeaguesPointsDao> getAllPointsWitIdsUsersForAllLeaguesRounds(List<Long> leaguesIds);

    @Query(value="SELECT ssp.competitor_id AS pointsOwnerId, " +
            "SUM(CASE WHEN ssp.league_match_points IS NULL " +
            "THEN 0 ELSE ssp.league_match_points end) AS leagueTotalPoints " +
            "FROM shooting_slots_points ssp " +
            "WHERE ssp.league_id = :leagueId GROUP BY ssp.competitor_id",
        nativeQuery = true)
    List<LeaguePointsSummaryDao> getTotalPointsWitIdsUsersForLeague(Long leagueId);

    @Query(value = "(SELECT ssp.competitor_id AS pointsOwnerId, ssp.league_id AS leagueId, " +
            "SUM(CASE WHEN ssp.league_match_points IS NULL THEN 0 ELSE ssp.league_match_points end) AS leagueTotalPoints " +
            "FROM shooting_slots_points ssp " +
            "WHERE ssp.league_id = :leagueId1 GROUP BY ssp.competitor_id, ssp.league_id " +
            "ORDER BY league_id, leagueTotalPoints ASC LIMIT :howMany) " +
            "UNION ALL " +
            "(SELECT ssp.competitor_id AS pointsOwnerId, ssp.league_id AS leagueId, " +
            "SUM(CASE WHEN ssp.league_match_points IS NULL THEN 0 ELSE ssp.league_match_points end) AS leagueTotalPoints " +
            "FROM shooting_slots_points ssp  " +
            "WHERE ssp.league_id = :leagueId2 GROUP BY ssp.competitor_id, ssp.league_id " +
            "ORDER BY league_id, leagueTotalPoints DESC limit :howMany)",nativeQuery = true)
    List<LeagueRelocateUsersDao> getBottomLeague1PlayersAndTopLeague2PlayersWithLeaguePoints(
            Long leagueId1, Long leagueId2, Integer howMany);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM shooting_slots_points ssp WHERE ssp.league_id = :leagueId", nativeQuery = true)
    void deleteAllLeagueSlotsPointsByLeagueId(Long leagueId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE shooting_slots_points SET league_id = :newLeagueId " +
            "WHERE league_id = :oldLeagueId AND competitor_id = :user1Id", nativeQuery = true)
    void updateLeagueIdForOneUserAllPoints(Long newLeagueId, Long oldLeagueId, Long user1Id);
}
