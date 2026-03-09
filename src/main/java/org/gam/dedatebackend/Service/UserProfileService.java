package org.gam.dedatebackend.Service;

import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {
    @Autowired
    UserProfileRepo userProfileRepo;

    public UserProfile findbyId(long id) {
        return userProfileRepo.findById(id).orElse(null);
    }

    public List<UserProfile> findALl() {
        return userProfileRepo.findAll();
    }

    public UserProfile createprofile(UserProfile userProfile) {
        return userProfileRepo.save(userProfile);
    }
}
