package org.gam.dedatebackend.Controller;


import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.DTO.Response.ProfileResponse;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.gam.dedatebackend.Service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/profile")
@RequiredArgsConstructor
public class UserController {
    private final ProfileService profileService;
    private final UserProfileRepo userProfileRepo;
    private final ContestRepo  contestRepo;
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.getProfileByEmail(email));
    }
    @GetMapping("/rooms")
    public List<ContestRoom> getAllRooms(Authentication authentication) {
        UserProfile userProfile=getUser(authentication);
        return contestRepo.findRoomsWhereUserIsHostOrTeam(userProfile.getId());
    }
    private UserProfile getUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authentication");
        }
        String username = authentication.getName(); // safest way
        return userProfileRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
