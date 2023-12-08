package hu.progmasters.blog.service.google;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ExplicitContentDetectionService {

    private final GoogleAuthService googleAuthService;

    public ExplicitContentDetectionService(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    public SafeSearchAnnotation detectExplicitContent(MultipartFile imageFile) throws IOException {

        GoogleCredentials credentials = googleAuthService.authGoogle();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(buildSettings(credentials))) {
            byte[] imageBytes = imageFile.getBytes();

            ByteString imgBytes = ByteString.copyFrom(imageBytes);
            Image image = Image.newBuilder().setContent(imgBytes).build();

            Feature safeSearchFeature = Feature.newBuilder().setType(Feature.Type.SAFE_SEARCH_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(safeSearchFeature)
                    .setImage(image)
                    .build();

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(
                    BatchAnnotateImagesRequest.newBuilder().addRequests(request).build()
            );

            return response.getResponses(0).getSafeSearchAnnotation();
        }
    }

    private ImageAnnotatorSettings buildSettings(GoogleCredentials credentials) throws IOException {
        return ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
    }
}