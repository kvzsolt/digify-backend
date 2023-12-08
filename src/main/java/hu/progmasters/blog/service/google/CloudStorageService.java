package hu.progmasters.blog.service.google;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Service
public class CloudStorageService {
    private Storage storage;

    @Value("${BUCKET_NAME}")
    private String bucketName;
    private final GoogleAuthService googleAuthService;

    public CloudStorageService(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
        initializeStorage();
    }

    private void initializeStorage() {
        try {
            GoogleCredentials credentials = googleAuthService.authGoogle();
            this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize google cloud storage", e);
        }
    }

    public String uploadFile(InputStream stream, String destinationBlobName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, destinationBlobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        try (WriteChannel writer = storage.writer(blobInfo)) {
            writer.write(ByteBuffer.wrap(stream.readAllBytes()));
        }

        return "gs://" +
                bucketName +
                "/" +
                destinationBlobName;
    }
}

