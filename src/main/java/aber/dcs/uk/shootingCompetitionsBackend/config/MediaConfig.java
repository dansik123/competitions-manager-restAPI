package aber.dcs.uk.shootingCompetitionsBackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Class which keep properties values stored in application.yml file
 * it only holds data in lower level of "media"
 */
@Configuration
@ConfigurationProperties("media")
public class MediaConfig {
    @Value("${media.images-storage-path}")
    private String imagesRootPath;

    public String getImagesRootPath() {
        return imagesRootPath;
    }
}
