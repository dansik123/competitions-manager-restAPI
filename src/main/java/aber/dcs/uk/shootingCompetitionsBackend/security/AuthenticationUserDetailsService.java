package aber.dcs.uk.shootingCompetitionsBackend.security;

import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @link <a href="https://github.com/bezkoder/spring-boot-refresh-token-jwt/blob/42b6976f11d69f1dfe088297fadd1140c697ce65/src/main/java/com/bezkoder/spring/security/jwt/security/services/UserDetailsServiceImpl.java">...</a>
 */
@Service
public class AuthenticationUserDetailsService implements UserDetailsService {
    private final UserRepository repository;
    private final static String USERNAME_NOT_FOUND_MSG = "User with email %s not found";

    public AuthenticationUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Method gets user's information from database
     * @param email the username identifying the user whose data is required.
     * @return UserDetails from database
     * @throws UsernameNotFoundException if user does not exists in database
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException(
                String.format(USERNAME_NOT_FOUND_MSG, email)
        ));
    }

    /**
     * Method checks if there input email has been registered in database
     * @param email String input
     * @return true if email is registered in database, false otherwise
     */
    public Boolean isUserEmailRegistered(String email){
        return repository.existsByEmail(email);
    }
}
