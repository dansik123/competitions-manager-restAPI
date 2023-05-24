package aber.dcs.uk.shootingCompetitionsBackend.repositories;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ClubDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.UserMemberDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMemberEntity, Long> {
    @Query(value = "SELECT id AS userId, firstname AS firstname, lastname AS lastname " +
            "FROM club_members " +
            "JOIN users ON club_members.member_id = users.id " +
            "WHERE club_members.club_id = :clubId",
            countQuery = "SELECT COUNT(*) FROM club_members " +
                    "WHERE club_members.club_id = :clubId",
            nativeQuery = true)
    Page<UserMemberDao> getUsersByClubId(Long clubId, Pageable pageable);

    @Query(value = "SELECT clubs.id AS id, clubs.club_name AS clubName FROM club_members " +
            "JOIN clubs ON club_members.club_id = clubs.id " +
            "WHERE club_members.member_id = :userId",
            nativeQuery = true)
    Optional<ClubDao> getClubByUserId(Long userId);

//    @Query(value = "SELECT clubMember FROM ClubMemberEntity clubMember " +
//            "WHERE member_id = :memberId AND club_id = :clubId",
//            nativeQuery = true)
//    boolean existsByIdAndClubId(Long memberId, Long clubId);
}
