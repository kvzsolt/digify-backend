package hu.progmasters.blog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hu.progmasters.blog.exception.imagevalidator.CloudinaryException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public List<String> uploadImages(List<MultipartFile> imageFiles) {

        List<String> imageUrls = new ArrayList<>();

        imageFiles.forEach(file -> {
            if (file != null && !file.isEmpty()) {
                Map uploadResult;
                try {
                    uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                } catch (IOException e) {
                    throw new CloudinaryException("Error uploading file");
                }
                String imageUrl = (String) uploadResult.get("url");
                imageUrls.add(imageUrl);
            }
        });

        return imageUrls;
    }
}
