package org.gam.dedatebackend.Service;

import org.gam.dedatebackend.DTO.Request.ProfileRequest;
import org.gam.dedatebackend.DTO.Response.ProfileResponse;

public interface ProfileService {
    public ProfileResponse createProfile(ProfileRequest profileRequest);
    ProfileResponse getProfileByEmail(String email);
    ProfileResponse getProfileById(Long id);
}
