package com.arsn.awsimageupload.datastore;

import com.arsn.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("7e9913b1-a49a-48f4-a192-3662b26036e4"), "Arjun Narayanasamy", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("5eb871c6-a282-4297-94bd-fe78f9410a2e"), "Vinithra Vijayakumar", null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}
