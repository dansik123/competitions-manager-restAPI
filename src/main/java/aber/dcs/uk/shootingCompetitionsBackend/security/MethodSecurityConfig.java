package aber.dcs.uk.shootingCompetitionsBackend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
//Class required with annotations above to enforce Authorization limitation of
//some methods execution using @PreAuthorize
public class MethodSecurityConfig {
}
