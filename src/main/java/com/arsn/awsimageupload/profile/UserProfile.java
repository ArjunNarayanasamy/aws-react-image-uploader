package com.arsn.awsimageupload.profile;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UserProfile {
    private final UUID userProfileId;
    private final String userName;
    private String userProfileImgLink;

    public UserProfile(UUID userProfileId, String userName, String userProfileImgLink) {
        this.userProfileId = userProfileId;
        this.userName = userName;
        this.userProfileImgLink = userProfileImgLink;
    }

    public UUID getUserProfileId() {
        return userProfileId;
    }

    public String getUserName() {
        return userName;
    }

    public Optional<String> getUserProfileImgLink() {
        return Optional.ofNullable(userProfileImgLink);
    }

    public void setUserProfileImgLink(String userProfileImgLink) {
        this.userProfileImgLink = userProfileImgLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(userProfileId, that.userProfileId) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(userProfileImgLink, that.userProfileImgLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfileId, userName, userProfileImgLink);
    }
}
