package org.gam.dedatebackend.Service;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.DTO.Request.contestCreationReq;
import org.gam.dedatebackend.Enum.roomStatus;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class roomService {


    private final UserProfileRepo userProfileRepo;
    private final ContestRepo contestRepo;
    private final livekitService livekitService;


    public ContestRoom createRoom(contestCreationReq contestCreationReq, Authentication authentication){
        UserProfile user = getUser(authentication);
        ContestRoom contestRoom = ContestRoom.builder()
                .id(generateString())
                .roomName(contestCreationReq.getRoomName())
                .host(user)
                .teamSize(contestCreationReq.getTeamSize())
                .totalParticipantsSize(contestCreationReq.getTeamSize()*2+1)
                .createdAt(Instant.now())
                .endTime(contestCreationReq.getEndTime())
                .roomStatus(roomStatus.LIVE)
                .currentParticipantsSize(0L)
                .debateType(contestCreationReq.getDebateType())
                .list(new ArrayList<>())
                .participants(new ArrayList<>())
                .build();
        livekitService.createLiveKitRoom(contestRoom);
        //saving room
        contestRepo.save(contestRoom);
        return contestRoom;
    }
    public ContestRoom createRoomOnlyRoom(contestCreationReq contestCreationReq, Authentication authentication){
        UserProfile user = getUser(authentication);
        ContestRoom contestRoom = ContestRoom.builder()
                .id(generateString())
                .roomName(contestCreationReq.getRoomName())
                .host(user)
                .teamSize(contestCreationReq.getTeamSize())
                .totalParticipantsSize(contestCreationReq.getTeamSize()*2+1)
                .createdAt(Instant.now())
                .endTime(contestCreationReq.getEndTime())
                .roomStatus(roomStatus.INITIALIZED)
                .currentParticipantsSize(0)
                .debateType(contestCreationReq.getDebateType())
                .list(new ArrayList<>())
                .participants(new ArrayList<>())
                .build();
        //saving room
        contestRepo.save(contestRoom);
        return contestRoom;
    }
    private UserProfile getUser(Authentication authentication) {
        String username = authentication.getName(); // safest way
        return userProfileRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
    public static String generateString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        StringBuilder result = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(chars.length());
            result.append(chars.charAt(index));
        }

        return result.toString();
    }
}
