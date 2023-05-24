package aber.dcs.uk.shootingCompetitionsBackend.security.jwt;

import aber.dcs.uk.shootingCompetitionsBackend.config.AuthConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtRefreshTokenProvider extends JwtTokenParser {
    private final AuthConfig authConfig;

    public JwtRefreshTokenProvider(AuthConfig authConfig) {
        super(authConfig.getRefreshTokenKey());
        this.authConfig = authConfig;
    }

    /**
     * Method creates new refresh token
     * @param authentication User's credentials, login and roles
     * @return Compacted JWT token as String
     */
    public String issueNewToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date expirationDate = new Date(now + authConfig.getRefreshTokenValidTime());

        return this.createNewToken(authentication.getName(), null, expirationDate);
    }

    /**
     * Method tries to extract JWS claims from JWT token
     * @param authToken JWT string token
     * @return JWS token claims
     * @throws JwtException There is problem with claims extraction
     */
    @Override
    public Claims extractTokenClaims(String authToken) throws JwtException {
        Claims bodyClaims;
        try {
            bodyClaims = this.jwtParser.parseClaimsJws(authToken).getBody();
        } catch (Exception e) {
            throw new JwtException("Refresh token: " + e.getMessage());
        }
        if(bodyClaims == null){
            throw new JwtException("Refresh token parsed token has no body claims");
        }
        return bodyClaims;
    }
}
