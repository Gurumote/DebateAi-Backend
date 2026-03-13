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
        UserProfile userProfile=ConvertToProfile(profileRequest);
        userProfile=userProfileRepo.save(userProfile);
        return convertToProfileResp(userProfile);
    }

    @Override
    public ProfileResponse getProfileByEmail(String email) {
        UserProfile user = userProfileRepo.findByemail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return convertToProfileResp(user);
    }

    @Override
    public ProfileResponse getProfileById(Long id) {
        UserProfile user = userProfileRepo.findById(id).get();
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return convertToProfileResp(user);
    }

    private ProfileResponse convertToProfileResp(UserProfile userProfile) {
        return ProfileResponse.builder()
                .Email(userProfile.getEmail())
                .username(userProfile.getUsername())
                .userid(String.valueOf(userProfile.getId()))
                .isActive(userProfile.isActive())
                .build();
    }

    private UserProfile ConvertToProfile(ProfileRequest profileRequest) {
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
