package aber.dcs.uk.shootingCompetitionsBackend.dao;

import org.springframework.beans.factory.annotation.Value;

/**
 * Class uses custom projection for competitor objects
 * @see <a href="https://medium.com/swlh/spring-data-jpa-projection-support-for-native-queries-a13cd88ec166">
 *     Projection Support</a>
 */
public interface RoundPauseUserDao {
    Integer getRoundNumber();
    @Value("#{@userShortMapper.buildShortUserDetails(target.user1Id, target.user1Firstname, target.user1Lastname)}")
    UserMemberDao pausedUser();
}
