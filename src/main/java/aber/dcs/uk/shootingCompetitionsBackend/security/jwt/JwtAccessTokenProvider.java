package aber.dcs.uk.shootingCompetitionsBackend.security.jwt;

import aber.dcs.uk.shootingCompetitionsBackend.config.AuthConfig;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtAccessTokenProvider extends JwtTokenParser {
    private static final String AUTHORITIES_KEY = "auth";
    private final AuthConfig authConfig;

    public JwtAccessTokenProvider(AuthConfig authConfig) {
        super(authConfig.getAccessTokenKey());
        this.authConfig = authConfig;
    }

    /**
     * Method creates new access token
     * @param authentication User's credentials, login and roles
     * @return Compacted JWT token as String
     */
    public String issueNewToken(Authentication authentication) {
        String authorities =
                authentication.
                        getAuthorities().
                        stream().
                        map(GrantedAuthority::getAuthority).
                        collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date expirationDate = new Date(now + authConfig.getAccessTokenValidTime());
        Map<String, String> tokenClaims = Map.of(AUTHORITIES_KEY, authorities);

        return ACCESS_TOKEN_PREFIX + this.createNewToken(
                authentication.getName(), tokenClaims, expirationDate);
    }

    /**
     * Methods extracts credentials and roles from JWT token
     * @param extractedClaims JWT extracted claims with username and authorities
     * @return Credentials and roles read from JWT token in Authentication object
     */
    public Authentication getAuthenticationFromAccessTokenClaims(Claims extractedClaims) {
        if(extractedClaims == null){
            throw new JwtException("Token claims does not exists");
        }
        if(!extractedClaims.containsKey(AUTHORITIES_KEY)){
            throw new JwtException("This is not access token");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(extractedClaims.get(AUTHORITIES_KEY).toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(extractedClaims.getSubject(), "", authorities);
    }

    /**
     * Methods removes Authorization token prefix
     * @param request HTTP request object
     * @return String with token or null if there is different or no prefix
     */
    public static String extractJwtTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        return removeTokenPrefix(bearerToken);
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
            throw new JwtException("Access token: " + e.getMessage());
        }
        if(bodyClaims == null){
            throw new JwtException("Access token parsed token has no body claims");
        }
        return bodyClaims;
    }
}
