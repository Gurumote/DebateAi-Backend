package org.gam.dedatebackend.Service;


import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.Contest.Participant.RoomParticipant;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.RoomParticipantRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class participantService {
    private final RoomParticipantRepo roomParticipantRepo;
    private final UserProfileRepo userProfileRepo;
    private final ContestRepo contestRepo;
    public RoomParticipant addParticipant(ContestRoom contestRoom,Authentication authentication,Team team) {
        UserProfile user=getUser(authentication);
        contestRoom.setCurrentParticipantsSize(contestRoom.getCurrentParticipantsSize()+ 1);
        contestRepo.save(contestRoom);
        RoomParticipant newParticipant= RoomParticipant.builder()
                .user(user)
                .joinedAt(Instant.now())
                .leftAt(null)
                .team(team)
                .contestRoom(contestRoom)
                .build();
        return roomParticipantRepo.save(newParticipant);
    }
    private UserProfile getUser(Authentication authentication) {
        String username = authentication.getName();
        return userProfileRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
