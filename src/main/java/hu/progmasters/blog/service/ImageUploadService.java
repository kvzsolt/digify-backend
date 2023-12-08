package hu.progmasters.blog.service;

import com.google.cloud.vision.v1.SafeSearchAnnotation;
import hu.progmasters.blog.dto.post.PostImage;
import hu.progmasters.blog.service.google.ExplicitContentDetectionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ImageUploadService {

    private ExplicitContentDetectionService contentDetectionService;

    public boolean uploadImage(PostImage postImage) throws IOException {
        List<MultipartFile> images = postImage.getImages();

        for (MultipartFile image : images) {
            SafeSearchAnnotation annotation = contentDetectionService.detectExplicitContent(image);

            if (!isContentSafe(annotation)) {
                return false;
            }
        }

        return true;

    }

    public boolean isContentSafe(SafeSearchAnnotation annotation) {
        List<String> expectedStrings = Arrays.asList(
                "adult: VERY_LIKELY", "adult: LIKELY",
                "violence: VERY_LIKELY", "violence: LIKELY",
                "racy: VERY_LIKELY", "racy: LIKELY"
        );

        List<String> annotationStrings = Arrays.asList(
                "adult: " + annotation.getAdult().name(),
                "violence: " + annotation.getViolence().name(),
                "racy: " + annotation.getRacy().name()
        );

        for (String expected : expectedStrings) {
            if (annotationStrings.contains(expected)) {
                return false;
            }
        }

        return true;
    }
}
