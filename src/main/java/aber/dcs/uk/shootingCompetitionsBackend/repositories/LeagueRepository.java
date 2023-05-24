package aber.dcs.uk.shootingCompetitionsBackend.repositories;

import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeagueRepository extends JpaRepository<LeagueEntity, Long> {
    @Query(value = "SELECT league_name AS leagueName " +
            "FROM leagues WHERE league_name IN :newLeaguesToSave", nativeQuery = true)
    List<String> getExistedLeaguesNamesByLeagueNamesList(List<String> newLeaguesToSave);

    @Query(value = "SELECT l.gun_type as gunType " +
                "FROM leagues l WHERE l.id = :leagueId", nativeQuery = true)
    Optional<String> getLeagueGunTypeById(Long leagueId);

    @Modifying
    @Query(value = "UPDATE leagues SET league_matches_generated = false, current_round_no = 1 WHERE id = :leagueId", nativeQuery = true)
    void updateLeagueMatchesGeneratedToFalse(Long leagueId);

    @Query(value="SELECT league_matches_generated FROM leagues WHERE id = :leagueId", nativeQuery = true)
    Optional<Boolean> getValueAreLeagueMatchesGeneratedByLeagueId(Long leagueId);

    @Query(value = "SELECT DISTINCT (regexp_matches(league_name, :regex))[:position] " +
            "FROM leagues", nativeQuery = true)
    List<String> leagueDistinctPrefixesByLeagueGunType(String regex, Integer position);

    List<LeagueEntity> findByNameLikeOrderByIdAsc(String name);

    @Query(value = "SELECT l.current_round_no FROM leagues l WHERE l.id = :leagueId", nativeQuery = true)
    Optional<Integer> findLeagueCurrentRoundIdByLeagueId(Long leagueId);
}
