package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

public abstract class HttpTest {
    protected final String rootURI = "/v1/api";
    protected static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(
                    MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);

    protected MockMvc mvcHttp;

    protected ObjectMapper objectMapper;

    protected String httpBodyParsedData;
}
