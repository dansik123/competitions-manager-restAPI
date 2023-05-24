package aber.dcs.uk.shootingCompetitionsBackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Class which keep properties values stored in application.yml file
 * it only holds data in lower level of "auth"
 */
@Configuration
@ConfigurationProperties("auth")
public class AuthConfig {
    @Value("${auth.access-token-key}")
    private String accessTokenKey;

    @Value("${auth.refresh-token-key}")
    private String refreshTokenKey;

    @Value("${auth.validity-time.access-token}")
    private Long accessTokenValidTime;

    @Value("${auth.validity-time.refresh-token}")
    private Long refreshTokenValidTime;

    public String getAccessTokenKey() {
        return accessTokenKey;
    }

    public void setAccessTokenKey(String accessTokenKey) {
        this.accessTokenKey = accessTokenKey;
    }

    public String getRefreshTokenKey() {
        return refreshTokenKey;
    }

    public void setRefreshTokenKey(String refreshTokenKey) {
        this.refreshTokenKey = refreshTokenKey;
    }

    public Long getAccessTokenValidTime() {
        return accessTokenValidTime;
    }

    public void setAccessTokenValidTime(Long accessTokenValidTime) {
        this.accessTokenValidTime = accessTokenValidTime;
    }

    public Long getRefreshTokenValidTime() {
        return refreshTokenValidTime;
    }

    public void setRefreshTokenValidTime(Long refreshTokenValidTime) {
        this.refreshTokenValidTime = refreshTokenValidTime;
    }
}
