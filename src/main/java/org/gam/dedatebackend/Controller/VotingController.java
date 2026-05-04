package org.gam.dedatebackend.Controller;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.gam.dedatebackend.Service.votesService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestController
@RequestMapping("api/vote")
@RequiredArgsConstructor
public class VotingController {
    private final UserProfileRepo userProfileRepo;
    private final ContestRepo contestRepo;
    private final votesService votesService;
    @PostMapping("/{roomId}/vote")
    public String vote(@PathVariable  String roomId, Authentication authentication, @RequestParam Team team){
        ContestRoom room=contestRepo.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        Instant currentTime = Instant.now();
        Instant endTime =room.getEndTime();
        if (endTime.isBefore(currentTime)) {
            throw new RuntimeException("Voting closed. Debate already ended");
        }
        UserProfile userProfile=getUser(authentication);
        votesService.votes(room,userProfile,team);
        return "Vote submitted successfully";
    }
    private UserProfile getUser(Authentication authentication) {
        String username = authentication.getName(); // safest way
        return userProfileRepo.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}