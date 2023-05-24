package aber.dcs.uk.shootingCompetitionsBackend.security.jwt;

import aber.dcs.uk.shootingCompetitionsBackend.exceptions.JwtAuthenticationException;
import aber.dcs.uk.shootingCompetitionsBackend.security.AuthenticationUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAccessTokenFilter extends OncePerRequestFilter {
    private final JwtAccessTokenProvider jwtAccessTokenProvider;
    private final JwtFilterEntryPoint jwtFilterEntryPoint;
    private final AuthenticationUserDetailsService authenticationUserDetailsService;

    public JwtAccessTokenFilter(JwtAccessTokenProvider jwtAccessTokenProvider,
                                JwtFilterEntryPoint jwtFilterEntryPoint,
                                AuthenticationUserDetailsService authenticationUserDetailsService) {
        this.jwtAccessTokenProvider = jwtAccessTokenProvider;
        this.jwtFilterEntryPoint = jwtFilterEntryPoint;
        this.authenticationUserDetailsService = authenticationUserDetailsService;
    }

    /**
     * Filter method executed each time new request comes to server
     * Method's task is to check user Authentication using JWT token
     * If token is OKAY and user exist in the Database then Authorization is successful
     * @link <a href="https://github.com/bezkoder/spring-boot-refresh-token-jwt/blob/master/src/main/java/com/bezkoder/spring/security/jwt/security/jwt/AuthTokenFilter.java">...</a>
     * @param request HTTP request coming to the server
     * @param response HTTP response which will get sent back
     * @param filterChain object with follows request and response to next filter
     * @throws IOException problem with I/O device
     * @throws ServletException problem with Servlet
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if(request.getRequestURI().matches("\\/v[0-9]*\\/api\\/refresh")){
            filterChain.doFilter(request, response);
            //for refresh request we care about only a cookie or request body
            //one of these two must have refreshToken in it
            //because there is posibily to have cookie
            //it is managed by Refresh Controller
            //TODO: YOU MIGHT NOT NEED THIS IF STATEMENT BECAUSE FILTER ONLY CHECKS AUTHORIZATION HEADER
            return;
        }
        try {
            String jwtToken = JwtAccessTokenProvider.extractJwtTokenFromRequest(request); //extract jwt token
            if(StringUtils.hasText(jwtToken)) {
                Claims jwtClaims = jwtAccessTokenProvider.extractTokenClaims(jwtToken); //check presence and validity
                String tokenUserEmail = jwtClaims.getSubject(); //extract subject(user's email from token)
                if(!authenticationUserDetailsService.isUserEmailRegistered(tokenUserEmail)){ //check if user exist in database
                    //rare edge case. indicates that access token was created outside this service
                    //therefore the signing key was probably stolen
                    throw new UsernameNotFoundException("User doesn't exists in the database");
                }
                Authentication authenticationToken =
                        jwtAccessTokenProvider.getAuthenticationFromAccessTokenClaims(jwtClaims); //get authentication token
                SecurityContextHolder.getContext().setAuthentication(authenticationToken); //set successfully authenticated user in security holder
            }
            filterChain.doFilter(request, response);
        }catch(Exception e){
            jwtFilterEntryPoint.commence(
                    request, response, new JwtAuthenticationException(e.getMessage()));
        }
    }
}
