package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.RegisterUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.AverageShootingScoreEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.RegistrationException;
import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.AverageShootingScoreRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.GeneralResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final AverageShootingScoreRepository averageShootingScoreRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                               AverageShootingScoreRepository averageShootingScoreRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.averageShootingScoreRepository = averageShootingScoreRepository;
    }

    /**
     * Methods registers new user in database
     * @param newUser users information
     * @return Response object with message about successful registration
     */
    public GeneralResponse signUpUser(RegisterUserDto newUser) throws RegistrationException
    {
        UserEntity entityNewUser = newUser.toUserEntity();
        if(userRepository.existsByEmail(entityNewUser.getEmail())) {
            throw new RegistrationException(
                    String.format("User with email %s already exists", newUser.getEmail()));
        }
        String encodedPassword = passwordEncoder.encode(entityNewUser.getPassword());
        entityNewUser.setPassword(encodedPassword);
        UserEntity registeredUser = userRepository.save(entityNewUser);

        //Add default average score for new user per all gun type available
        AverageShootingScoreEntity averageShootingScore;
        for(GunType gunType: GunType.values()){
            averageShootingScore = new AverageShootingScoreEntity(
                    null, registeredUser, BigDecimal.valueOf(0.00), gunType);
            averageShootingScoreRepository.save(averageShootingScore);
        }

        return new GeneralResponse(
                String.format("User %s registered successfully", registeredUser.getEmail()));
    }
}
