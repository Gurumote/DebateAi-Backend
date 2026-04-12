package org.gam.dedatebackend.Service;


import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Model.Contest.RoomParticipant;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class RoomParticipantService {
    private final ContestRepo contestRepo;
    private final UserProfileRepo userProfileRepo;
    public RoomParticipant createJoinReq(String roomId, Authentication authentication) {

        if (roomId == null || roomId.isBlank()) {
            throw new RuntimeException("Room ID is required");
        }
        ContestRoom room = contestRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        Long userId = getUserid(authentication);

        return RoomParticipant.builder()
                .contestRoom(getRoomId(roomId))
                .hostId(room.getHostId())
                .userId(userId)
                .joinedAt(new Timestamp(System.currentTimeMillis()))
                .leftAt(null)
                .build();
    }
    public RoomParticipant createJoinReqByUserId(String roomId, Long userId) {

        if (roomId == null || roomId.isBlank()) {
            throw new RuntimeException("Room ID is required");
        }
        ContestRoom room = contestRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        return RoomParticipant.builder()
                .contestRoom(getRoomId(roomId))
                .hostId(room.getHostId())
                .userId(userId)
                .joinedAt(new Timestamp(System.currentTimeMillis()))
                .leftAt(null)
                .build();
    }

    private ContestRoom getRoomId(String roomId) {
        ContestRoom room= contestRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        return room;
    }

    public long getUserid(Authentication authentication) {
        String email = authentication.getName();
        UserProfile user = userProfileRepo.findByemail(email);
        return user.getId();
    }
}
