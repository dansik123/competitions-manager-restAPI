package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.entities.RefreshTokenEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.JwtAuthenticationException;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.RefreshTokenRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtAccessTokenProvider;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtRefreshTokenProvider;
import aber.dcs.uk.shootingCompetitionsBackend.security.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthJwtTokensManagerService {
    private final JwtAccessTokenProvider jwtAccessTokenProvider;
    private final JwtRefreshTokenProvider jwtRefreshTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final static String REFRESH_TOKEN_MAP_KEY = "refreshToken";
    private final static String ACCESS_TOKEN_MAP_KEY = "accessToken";

    public AuthJwtTokensManagerService(JwtAccessTokenProvider jwtAccessTokenProvider,
                                       JwtRefreshTokenProvider jwtRefreshTokenProvider,
                                       RefreshTokenRepository refreshTokenRepository,
                                       UserRepository userRepository) {
        this.jwtAccessTokenProvider = jwtAccessTokenProvider;
        this.jwtRefreshTokenProvider = jwtRefreshTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Methods generates refresh and access tokens for Authentication of other REST HTTP calls
     * @param authentication user's authorization contains userName, password and user's roles
     * @param generateRefreshTokenForCookie Boolean if is true it will create refresh token without 'Bearer prefix to let it set for HTTP cookie'
     *                                      otherwise refresh token will have 'Bearer ' prefix to let send it in HTTP body
     * @return key value pair Map contains 2 entries with refreshToken and accessToken
     * @throws AuthenticationException with information about fail of JWT token generation
     */
    public Map<String,String> generateUserJwtTokens(
            Authentication authentication,
            boolean generateRefreshTokenForCookie) throws AuthenticationException {

        //Check if user is authenticated
        if(!authentication.isAuthenticated()){
            throw new AuthenticationServiceException("Can't create tokens for unauthenticated user");
        }

        //get user by it's email
        UserEntity user = userRepository.findByEmail(SecurityUtils.extractUserLogin(authentication)).orElseThrow(
                () -> new UsernameNotFoundException("Token signed with user who does not exists in database"));

        //Generate Refresh and access tokens for authenticated user
        String accessToken = jwtAccessTokenProvider.issueNewToken(authentication);
        String refreshToken = jwtRefreshTokenProvider.issueNewToken(authentication);

        //Add refresh token with user to database
        refreshTokenRepository.save(new RefreshTokenEntity(refreshToken, user));

        if(!generateRefreshTokenForCookie){ //add prefix for refresh token which will get send back in response body
            refreshToken = JwtRefreshTokenProvider.getTokenPrefix() + refreshToken;
        }
        return new HashMap<>(Map.of(
            ACCESS_TOKEN_MAP_KEY, accessToken,
            REFRESH_TOKEN_MAP_KEY, refreshToken));
    }

    /**
     * Methods creates new access token from request body Map or Http cookie if first one does not exist
     * @param request HTTP request used for getting refresh token from cookie if Map doesn't have it
     * @param requestBodyMap Map contains one entry with refreshToken
     * @return Map contains one entry with accessToken
     * @throws JwtAuthenticationException token is null or does not contain token prefix
     * @throws JwtException problem with token parsing
     * @throws UsernameNotFoundException user associated with email in token doesn't exist in database
     */
    public Map<String, String> issueAccessTokenFromCookieOrRequestBody(
            HttpServletRequest request,
            Map<String, String> requestBodyMap)
            throws JwtAuthenticationException, JwtException, UsernameNotFoundException {
        String refreshToken;

        if(requestBodyMap != null && requestBodyMap.containsKey(REFRESH_TOKEN_MAP_KEY)){
            refreshToken = JwtRefreshTokenProvider.
                    removeTokenPrefix(requestBodyMap.get(REFRESH_TOKEN_MAP_KEY));
        }else {
            if(request.getCookies() == null){ //check if cookie is set
                throw new JwtAuthenticationException("No cookie set and no body refreshToken for " +
                        "refresh. Please login again");
            }
            refreshToken = //get cookie refreshToken value
                    Arrays.stream(request.getCookies())
                            .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_MAP_KEY))
                            .findFirst().map(Cookie::getValue).orElseThrow(
                                    () -> new JwtAuthenticationException(
                                            "No cookie set and no body refreshToken for " +
                                                    "refresh. Please login again"));
        }
        return issueNewAccessTokenFromRefreshToken(refreshToken);
    }

    /**
     * Method generates Access token using refresh token string
     * @param refreshToken JWT string token without 'Bearer prefix'
     * @return new access token
     * @throws JwtAuthenticationException token is null or does not contain token prefix
     * @throws JwtException problem with token parsing
     * @throws UsernameNotFoundException user associated with email in token doesn't exist in database
     */
    public Map<String, String> issueNewAccessTokenFromRefreshToken(String refreshToken)
            throws JwtAuthenticationException, JwtException, UsernameNotFoundException{

        if(refreshToken == null){
            throw new JwtAuthenticationException("Refresh token doesn't contain 'Bearer ' prefix or is just null");
        }
        Claims tokenClaims = jwtRefreshTokenProvider.extractTokenClaims(refreshToken); //get token claims. If something
        //is wrong with token parsing error will be thrown

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new AuthenticationServiceException("Refresh token used after logout token(please login again)")); //check if token has been saved in database

        if(!tokenClaims.getSubject().equals(refreshTokenEntity.getOwner().getEmail())){
            //Very SUSPICIOUS this might mean that token secret was stolen which might cause security issues
            //WARNING
            throw new AuthenticationServiceException("Token subject doesn't match with user's email");
        }

            Authentication authentication = //create authorization object for founded user
                    new UsernamePasswordAuthenticationToken(
                            refreshTokenEntity.getOwner().getUsername(),
                            refreshTokenEntity.getOwner().getPassword(),
                            refreshTokenEntity.getOwner().getAuthorities());
            String newAccessToken = jwtAccessTokenProvider.issueNewToken(authentication); //issue new access token for user
            return Map.of(ACCESS_TOKEN_MAP_KEY, newAccessToken);
    }

    /**
     * Getter for HashMap key to access the refresh token
     * @return String with key name for use in HashMap
     */
    public static String getRefreshTokenMapKey(){
        return REFRESH_TOKEN_MAP_KEY;
    }

    /**
     * Getter for HashMap key to access the access token
     * @return String with key name for use in HashMap
     */
    public static String getAccessTokenMapKey(){
        return ACCESS_TOKEN_MAP_KEY;
    }
}
