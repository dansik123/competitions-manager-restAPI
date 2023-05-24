package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class GunTypeService {

    //TODO: Might need change to entity in the future for more robust solution

    /**
     * Method converts GunType values to array of Strings
     * @return List with all gunTypes declared in {@link GunType}
     */
    public List<String> getAvailableGunTypes(){
        return Arrays.stream(GunType.values()).
                map(Enum::toString).
                collect(Collectors.toList());
    }
}
