package com.arsn.awsimageupload.profile;

import com.arsn.awsimageupload.buckets.BucketName;
import com.arsn.awsimageupload.filestore.FileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;
import static org.apache.http.entity.ContentType.IMAGE_GIF;

@Service
public class UserProfileService {

    private static Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);
        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());

        return user.getUserProfileImgLink()
                .map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);
    }

    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        // Check if img is not empty
        isFileEmpty(file);

        // If file is an Image
        isImage(file);

        // If the user exists in db
        UserProfile user = getUserProfileOrThrow(userProfileId);

        // Grab metadata from file if any
        Map<String, String> metadata = getMetadata(file);

        // Store the img in S3 and update database with s3 image link
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
        String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
        try {
            fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
            user.setUserProfileImgLink(filename);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Map<String, String> getMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("content-type", file.getContentType());
        metadata.put("content-length", String.valueOf(file.getSize()));
        return metadata;
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId) {
        return this.userProfileDataAccessService.getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));
    }

    private void isImage(MultipartFile file) {
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            logger.warn("Only images are allowed, received - {} file", file.getContentType());
            throw new IllegalStateException("The file should be an Image");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            logger.warn("The uploaded file is empty! , filename - {}", file.getOriginalFilename());
            throw new IllegalStateException("Cannot upload empty file, size [{" + file.getSize() + "}]");
        }
    }

}
