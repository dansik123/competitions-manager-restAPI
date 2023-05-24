package aber.dcs.uk.shootingCompetitionsBackend.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public abstract class JwtTokenParser {
    protected final JwtParser jwtParser;
    protected final Key key;
    protected static final String ACCESS_TOKEN_PREFIX = "Bearer ";
    private static final String JWT_TOKEN_ISSUER = "Shooting competition service";

    protected JwtTokenParser(String jwtTokenKey) {
        byte[] keyBytes;
        if (ObjectUtils.isEmpty(jwtTokenKey)) { //Check if Jwt secret has been set
            throw new RuntimeException("Access token sign key was not set");
        }
        keyBytes = jwtTokenKey.getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(keyBytes); //prepare signing key for JWT tokens
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build(); //create new JWT parser for signing and reading new tokens
    }

    /**
     * Method try's to validate JWT token
     * @param authToken JWT string token
     * @return True if validation is successful(token is a string and has been parsed without any problems)
     * @throws JwtException if there is missing jwt token string or token parsing failed
     */
    public Claims extractTokenClaims(String authToken) throws JwtException {
        Claims bodyClaims = jwtParser.parseClaimsJws(authToken).getBody();
        if(bodyClaims == null){
            throw new JwtException("Parsed token has no body claims");
        }
        return bodyClaims;
    }

    /**
     * Methods builds JWT token from provided parameters
     * @param tokenSubject token Subject (expected to have user's email)
     * @param claims token Claims (key value pairs)
     * @param expirationDate date when token should expire
     * @return JWT token string
     */
    protected String createNewToken(String tokenSubject, Map<String, String> claims, Date expirationDate) {
        JwtBuilder builder = Jwts.builder();
        if(claims != null){
            for(String claimKey: claims.keySet()){
                builder.claim(claimKey, claims.get(claimKey));
            }
        }

        return builder
                .setSubject(tokenSubject)
                .setIssuer(JWT_TOKEN_ISSUER)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expirationDate)
                .compact();
    }

    /**
     * Method removes Authorization prefix form JWT token string
     * @param tokenWithPrefix token with Authorization prefix
     * @return JWT token string with no prefix or null if input sting didn't have prefix
     */
    public static String removeTokenPrefix(String tokenWithPrefix){
        if (StringUtils.hasText(tokenWithPrefix) && tokenWithPrefix.startsWith(ACCESS_TOKEN_PREFIX)) {
            return tokenWithPrefix.substring(7);
        }
        return null;
    }

    /**
     * Getter for static Authorization prefix
     * @return Authorization prefix string "Bearer"
     */
    public static String getTokenPrefix(){
        return ACCESS_TOKEN_PREFIX;
    }
}
