package org.gam.dedatebackend.Controller;


import org.gam.dedatebackend.DTO.Response.ProfileResponse;
import org.gam.dedatebackend.Service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/profile")
public class UserController {

    @Autowired
    ProfileService profileService;
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.getProfileByEmail(email));
    }
}
