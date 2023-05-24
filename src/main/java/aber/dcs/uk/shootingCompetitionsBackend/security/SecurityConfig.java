package aber.dcs.uk.shootingCompetitionsBackend.security;

import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtAccessTokenFilter;
import aber.dcs.uk.shootingCompetitionsBackend.security.jwt.JwtFilterEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAccessTokenFilter jwtAccessTokenFilter;
    private final JwtFilterEntryPoint jwtFilterEntryPoint;
    private final SecurityCustomAccessDeniedHandler securityCustomAccessDeniedHandler;
    private final AuthenticationUserDetailsService authenticationUserDetailsService;
    private final String allowedCorsOrigin;


    public SecurityConfig(
            JwtAccessTokenFilter jwtAccessTokenFilter,
            JwtFilterEntryPoint jwtFilterEntryPoint,
            SecurityCustomAccessDeniedHandler securityCustomAccessDeniedHandler,
            AuthenticationUserDetailsService authenticationUserDetailsService,
            @Value("${cors-allow-origin}") String allowedCorsOrigin) {
        this.jwtAccessTokenFilter = jwtAccessTokenFilter;
        this.jwtFilterEntryPoint = jwtFilterEntryPoint;
        this.securityCustomAccessDeniedHandler = securityCustomAccessDeniedHandler;
        this.authenticationUserDetailsService = authenticationUserDetailsService;
        this.allowedCorsOrigin = allowedCorsOrigin;
    }

    /**
     * Method sets up spring security filter chain configuration allowing
     * resolve CROSS ORIGIN requests,
     * sets session policy, excluding requests paths from authorization
     * and adds new authorization filter to the chain
     * @param http Http Security restrictions object
     * @return Filter chain used to match with HttpServletRequests to allow or deny access
     * @throws Exception problem to create HttpSecurity
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/v*/api/register/**",
                        "/v*/api/login/**",
                        "/v*/api/logout/**",
                        "/v*/api/refresh/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/v*/api/matches/*/competitor*/scorecard")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and().exceptionHandling()
                .accessDeniedHandler(securityCustomAccessDeniedHandler)
                .authenticationEntryPoint(jwtFilterEntryPoint).and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAccessTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Methods creates AuthenticationProvider configured with
     * Authentication check against users stored in configured database
     * @return AuthenticationProvider with authentication of users in database and
     * password encoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(authenticationUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * Method creates manager for authentication
     * @param configuration Spring boot injected authentication configuration
     * @return AuthenticationManager
     * @throws Exception problem to get AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Default encoder object used to encode passwords in the database
     * @return PasswordEncoder of type BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Custom CORS settings for front-end app
     * @{link <a href="https://stackoverflow.com/questions/36968963/how-to-configure-cors-in-a-spring-boot-spring-security-application/66590699#66590699">...</a>}
     * @return CorsConfigurationSource with custom CORS
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(allowedCorsOrigin));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
