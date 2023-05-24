package aber.dcs.uk.shootingCompetitionsBackend.dtos;

import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Objects;

public class ImageMediaDto {
    private MediaType mediaType;
    private byte[] imageContent;

    public ImageMediaDto() {
    }

    public ImageMediaDto(MediaType mediaType, byte[] imageContent) {
        this.mediaType = mediaType;
        this.imageContent = imageContent;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public byte[] getImageContent() {
        return imageContent;
    }

    public void setImageContent(byte[] imageContent) {
        this.imageContent = imageContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageMediaDto that = (ImageMediaDto) o;
        return Objects.equals(mediaType, that.mediaType) && Arrays.equals(imageContent, that.imageContent);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mediaType);
        result = 31 * result + Arrays.hashCode(imageContent);
        return result;
    }
}
