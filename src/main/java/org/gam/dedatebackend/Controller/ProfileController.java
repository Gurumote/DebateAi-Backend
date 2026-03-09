package org.gam.dedatebackend.Controller;


import org.gam.dedatebackend.Model.ProfileRequest;
import org.gam.dedatebackend.Model.ProfileResponse;
import org.gam.dedatebackend.Service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createProfile(@RequestBody ProfileRequest profileRequest) {
        ProfileResponse profileResponse = profileService.createProfile(profileRequest);
        return profileResponse;
    }
}
