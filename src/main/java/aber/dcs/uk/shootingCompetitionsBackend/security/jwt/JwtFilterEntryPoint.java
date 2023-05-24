package aber.dcs.uk.shootingCompetitionsBackend.security.jwt;

import aber.dcs.uk.shootingCompetitionsBackend.responses.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static aber.dcs.uk.shootingCompetitionsBackend.config.GlobalExceptionHandler.FORBIDDEN_AUTH_PREFIX;

@Component
public class JwtFilterEntryPoint implements AuthenticationEntryPoint {
    /**
     * Exits Spring security filters chain cased by Authorization JWT filter exceptions
     * @param request that resulted in an <code>AuthenticationException</code>
     * @param response so that the user agent can begin authentication
     * @param authException that caused the invocation
     * @throws IOException problem with I/O device
     * @throws ServletException problem with Servlet
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        ErrorResponse re = new ErrorResponse(
                HttpStatus.FORBIDDEN.toString(), FORBIDDEN_AUTH_PREFIX + authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, re);
        responseStream.flush();
    }
}
