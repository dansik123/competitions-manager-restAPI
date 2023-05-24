package aber.dcs.uk.shootingCompetitionsBackend.security;

import aber.dcs.uk.shootingCompetitionsBackend.responses.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class SecurityCustomAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * Method handle all access denied request which may occur during security filter checks
     *  or during PreAuthorize check
     * @param request input HTTP request
     * @param response output HTTP response
     * @param accessDeniedException Exception which keeps information what caused Access denied error
     * @throws IOException problem to read response object data
     * @throws ServletException problem with Java Servlet Server interface
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorResponse re = new ErrorResponse(HttpStatus.UNAUTHORIZED.toString(), accessDeniedException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, re);
        responseStream.flush();
    }
}
