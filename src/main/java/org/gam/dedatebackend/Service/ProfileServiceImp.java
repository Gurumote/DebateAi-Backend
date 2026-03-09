package org.gam.dedatebackend.Service;

import org.gam.dedatebackend.Model.ProfileRequest;
import org.gam.dedatebackend.Model.ProfileResponse;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service

public class ProfileServiceImp implements ProfileService {

    @Autowired
    private UserProfileRepo userProfileRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public ProfileResponse createProfile(ProfileRequest profileRequest) {
        UserProfile userProfile=converToProfile(profileRequest);
        userProfile=userProfileRepo.save(userProfile);
        return convertToProfileRespo(userProfile);
    }

    private ProfileResponse convertToProfileRespo(UserProfile userProfile) {
        return ProfileResponse.builder()
                .Email(userProfile.getEmail())
                .username(userProfile.getUsername())
                .userid(String.valueOf(userProfile.getId()))
                .isActive(userProfile.isActive())
                .build();
    }

    private UserProfile converToProfile(ProfileRequest profileRequest) {
        return UserProfile.builder()
                .email(profileRequest.getEmail())
                .password(passwordEncoder.encode(profileRequest.getPassword()))
                .username(profileRequest.getName())
                .isActive(true)
                .resetotp(null)
                .verifyotp(null)
                .resetotpExpiration(0L)
                .verifyotpExpiration(0L)
                .build();
    }
}
