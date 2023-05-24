package aber.dcs.uk.shootingCompetitionsBackend.repositories;

import aber.dcs.uk.shootingCompetitionsBackend.dao.*;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueCompetitorsEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.keys.LeagueCompetitorsKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueCompetitorsRepository extends JpaRepository<LeagueCompetitorsEntity, LeagueCompetitorsKey> {
    @Query(value = "SELECT u.id AS userId, u.firstname AS firstname, u.lastname AS lastname, " +
                    "ass.gun_type AS gunType, ass.avg_score AS avgScore, " +
                    "(ROW_NUMBER() OVER (ORDER BY avg_score DESC) - 1) / :groupSize + 1 AS groupId " +
                    "FROM users u " +
                    "JOIN avg_shooting_scores ass ON u.id = ass.user_id " +
                    "WHERE u.id IN :competitorsList AND ass.gun_type = :gunType " +
                    "ORDER BY avgScore DESC",
            nativeQuery = true)
    List<LeagueCompetitorGenerateGroupsDao> getLeagueGroupsByGunTypeOrderedByAverageShootingScore(
            List<Long> competitorsList, String gunType, Integer groupSize);

    @Query(value = "SELECT l.id AS leagueId, l.gun_type AS leagueGunType, " +
            "l.league_name AS leagueName, l.max_competitors AS leagueMaxCompetitors " +
            "l.competitors_count AS competitorsCount, l.expected_rounds_to_play AS totalRounds " +
            "l.current_round_no AS currentRoundNo " +
            "FROM league_competitors lc " +
            "JOIN leagues l ON lc.league_id = l.id " +
            "WHERE lc.competitor_id = :userId",
            nativeQuery = true)
    List<LeagueDao> getLeaguesAssociatedWithUser(Long userId);

    @Query(value="SELECT u.id AS userId, u.firstname AS firstname, " +
            "u.lastname AS lastname, ass.avg_score AS avgScore " +
            "FROM league_competitors lc " +
            "JOIN users u ON u.id = lc.competitor_id " +
            "JOIN avg_shooting_scores ass ON lc.competitor_id = ass.user_id " +
            "WHERE lc.league_id = :leagueId AND ass.gun_type = :gunType " +
            "ORDER BY lastname ASC", nativeQuery = true)
    List<LeagueCompetitorDao> getLeagueCompetitorsWithAvgScoreByLeagueId(Long leagueId, String gunType);

    @Query(value="SELECT lc.competitor_id " +
            "FROM league_competitors lc " +
            "WHERE lc.league_id = :leagueId ", nativeQuery = true)
    List<Long> getLeagueCompetitorsIdsByLeagueId(Long leagueId);

    @Query(value="SELECT u.id AS userId, u.firstname AS firstname, u.lastname AS lastname " +
            "FROM league_competitors lc " +
            "JOIN users u ON lc.competitor_id = u.id " +
            "WHERE lc.league_id = :leagueId ", nativeQuery = true)
    List<UserMemberDao> getLeagueCompetitorsByLeagueId(Long leagueId);
    @Query(value="SELECT u.id AS userId, u.firstname AS firstname, u.lastname AS lastname " +
            "FROM league_competitors lc " +
            "JOIN users u ON lc.competitor_id = u.id " +
            "WHERE lc.league_id in :leaguesIds", nativeQuery = true)
    List<UserMemberDao> getAllLeaguesCompetitors(List<Long> leaguesIds);

    @Query(value="SELECT l.id AS leagueId, l.league_name AS leagueName " +
            "FROM league_competitors lc " +
            "JOIN leagues l on l.id = lc.league_id " +
            "WHERE lc.competitor_id = :userId", nativeQuery = true)
    List<LeagueSelectDao> getAllLeaguesAssociatedWithUser(Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE league_competitors SET league_id = :newLeagueId " +
            "WHERE league_id = :oldLeagueId AND competitor_id = :user1Id", nativeQuery = true)
    void updateLeagueIdForOneUser(Long newLeagueId, Long oldLeagueId, Long user1Id);

}
