package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.config.MediaConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;

@Component
public class ScoreCardsImagesService {
    private final MediaConfig mediaConfig;
    private final Clock clock;

    public ScoreCardsImagesService(MediaConfig mediaConfig, Clock clock) {
        this.mediaConfig = mediaConfig;
        this.clock = clock;
    }

    /**
     * Method reads images of score-card saved on server location
     * @param imageFileName image filename
     * @return image data as byte array
     * @throws IOException problem to find or read image data
     */
    public byte[] readStoredScoreCardImage(String imageFileName) throws IOException {
        Path imageFullPath = Path.of(mediaConfig.getImagesRootPath(), imageFileName);
        FileInputStream fileInputStream = new FileInputStream(imageFullPath.toFile());
        byte[] imageBytes = fileInputStream.readAllBytes();
        fileInputStream.close();
        return imageBytes;
    }

    /**
     * Method saves new image score-card on the server
     * @param multipartFile image data
     * @throws IOException problem to save new image on the server
     */
    public String storeMultiPartImage(MultipartFile multipartFile) throws IOException{
        Path imageFullPath = Path.of(
                mediaConfig.getImagesRootPath(),
                clock.millis() + "_" + multipartFile.getOriginalFilename());
        File file  = imageFullPath.toFile();
        FileOutputStream fileInputStream = new FileOutputStream(imageFullPath.toFile());
        fileInputStream.write(multipartFile.getBytes());
        fileInputStream.close();
        return file.getName();
    }
}
