package aber.dcs.uk.shootingCompetitionsBackend.dao;

import aber.dcs.uk.shootingCompetitionsBackend.responses.UserMemberResponse;
import org.springframework.stereotype.Component;

/**
 * Class helps to create user Dao objects for competitors in interface {@link ShootingSlotDao}
 */
@Component
public class UserShortMapper {
    public UserMemberDao buildShortUserDetails(Long id, String firstname, String lastname){
        return new UserMemberResponse(id, firstname, lastname);
    }
}
