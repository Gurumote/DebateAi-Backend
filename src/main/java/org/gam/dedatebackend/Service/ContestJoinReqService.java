package org.gam.dedatebackend.Service;


import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Contest.ContestJoinReq;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Model.Contest.RoomParticipant;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class ContestJoinReqService {
    private final ContestRepo contestRepo;
    private final UserProfileRepo userProfileRepo;
    public RoomParticipant createJoinReq(ContestJoinReq contestJoinReq, Authentication authentication) {
        String roomId = contestJoinReq.getRoomId();
        System.out.println("ROOM ID: " + contestJoinReq.getRoomId());
        if (roomId == null || roomId.isBlank()) {
            throw new RuntimeException("Room ID is required");
        }
        ContestRoom room = contestRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        Long userId = getUserid(authentication);

        return RoomParticipant.builder()
                .roomId(room.getId())
                .hostId(room.getHostId())
                .userId(userId)
                .joinedAt(new Timestamp(System.currentTimeMillis()))
                .leftAt(null)
                .build();
    }
    public long getUserid(Authentication authentication) {
        String email = authentication.getName();
        UserProfile user = userProfileRepo.findByemail(email);
        return user.getId();
    }
}
