package aber.dcs.uk.shootingCompetitionsBackend.helpers.annotations;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithMockUser(username = "spectator@nano.com", authorities = { "SPECTATOR" })
public @interface UseMockSpectatorAuth {
}
