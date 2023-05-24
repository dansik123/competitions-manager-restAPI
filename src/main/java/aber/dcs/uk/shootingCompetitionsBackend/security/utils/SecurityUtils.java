package aber.dcs.uk.shootingCompetitionsBackend.security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Optional;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user or Optional.empty()
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractUserLogin(securityContext.getAuthentication()));
    }

    /**
     * Methods gets login from currently authenticated user
     * @param authentication user details and principles
     * @return user login as String or null
     */
    public static String extractUserLogin(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }else if(authentication.getPrincipal() instanceof UserDetails){
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
// At the beginning this method was written to ensure security of individual user data
    // by limit access to only current user data
    // using method above getCurrentUserLogin() we ensures that we deal only with current user data
    // therefore this method is not needed anymore
//    /**
//     * Method checks if user have enough privileges to access the resource
//     * @param userRepository UserRepository to check user details in database
//     * @param requestUserId userId used to display resource
//     * @param allowedAccessRoles role privilege which allows access to resource
//     * @return True if user try to access resource for its own data or user have enough privileges to get access
//     *  otherwise False
//     * @throws AuthenticationServiceException problem with Authentication token in security context
//     * @throws CustomHttpException if user was not found in the database
//     */
//    public static boolean allowAccessResource(
//            UserRepository userRepository, Long requestUserId, Role... allowedAccessRoles)
//            throws AuthenticationServiceException, CustomHttpException {
//        String currentUserEmail = SecurityUtils.getCurrentUserLogin().orElseThrow(
//                () -> new AuthenticationServiceException("User's authentication does not exists"));
//        UserEntity currentUserEntity = userRepository.findByEmail(currentUserEmail).orElseThrow(() ->
//                new CustomHttpException(
//                        String.format("User with email %s not found", currentUserEmail),
//                        HttpStatus.NOT_FOUND));
//        List<GrantedAuthority> allowedRolesAuthorities =
//                Arrays.stream(allowedAccessRoles).
//                        map(role -> new SimpleGrantedAuthority(role.name())).
//                        collect(Collectors.toList());
//        //user's can only access their own resources
//        //other more privileged users can have access too. Depends on kind of resource
//        return currentUserEntity.getId().equals(requestUserId) ||
//                currentUserEntity.getAuthorities().stream().anyMatch(allowedRolesAuthorities::contains);
//    }
}