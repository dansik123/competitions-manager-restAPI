package aber.dcs.uk.shootingCompetitionsBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Clock server configuration to help store images with the same name on the server
 * @see aber.dcs.uk.shootingCompetitionsBackend.services.ScoreCardsImagesService
 */
@Configuration
public class ClockConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
