package hu.progmasters.blog.dto.post;

import hu.progmasters.blog.validator.imagevalidator.Image;
import hu.progmasters.blog.validator.imagevalidator.MaxSize;
import hu.progmasters.blog.validator.imagevalidator.NotEmptyList;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class PostImage {
    @NotEmptyList(message = "Images list must not be empty")
    @MaxSize(message = "Size max 5MB/file")
    @Image(message = "Only JPG/PNG/JPEG accepted")
    private List<MultipartFile> images;
}
