package aber.dcs.uk.shootingCompetitionsBackend.dao;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Date;

/**
 * Class uses custom projection for competitor objects
 * @see <a href="https://medium.com/swlh/spring-data-jpa-projection-support-for-native-queries-a13cd88ec166">
 *     Projection Support</a>
 */
public interface ShootingSlotDao {
    Long getId();
    @Value("#{@userShortMapper.buildShortUserDetails(target.user1Id, target.user1Firstname, target.user1Lastname)}")
    UserMemberDao getCompetitor1();
    @Value("#{@userShortMapper.buildShortUserDetails(target.user2Id, target.user2Firstname, target.user2Lastname)}")
    UserMemberDao getCompetitor2();
    Date getSlotDate();
    Integer getRoundNumber();
    @Value("#{(target.hasScoreResult)? target.competitor1Score + '-' + target.competitor2Score : \"unmarked\"}")
    String getSlotMatchResult();
    String getCompetitor1ScoreCardLink();
    String getCompetitor2ScoreCardLink();
}
