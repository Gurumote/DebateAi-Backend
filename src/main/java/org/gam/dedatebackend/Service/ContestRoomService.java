package org.gam.dedatebackend.Service;


import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.DTO.Request.contestCreationReq;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Enum.roomStatus;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.Contest.Participant.RoomParticipant;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.gam.dedatebackend.Util.LiveKitUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.Instant;


@Service
@RequiredArgsConstructor
public class ContestRoomService {
    private final roomService roomService;
    private final participantService participantService;
    private final LiveKitUtil liveKitUtil;
    private final ContestRepo contestRepo;
    private final UserProfileRepo  userProfileRepo;
    public String createRoomAndGenerateTokenOfHost(contestCreationReq contestCreationReq, Authentication authentication){
        //Creating room in the DB
        ContestRoom contestRoom=roomService.createRoom(contestCreationReq, authentication);
        contestRoom.setSetLiveAt(Instant.now());
        contestRepo.save(contestRoom);
        UserProfile profile=getUserProfile(authentication);
        //adding Host in the Room(DB)
        String token=liveKitUtil.generateToken(contestRoom, String.valueOf(profile.getId()),Team.HOST);
        RoomParticipant roomParticipant=participantService.addParticipant(contestRoom,authentication, Team.HOST);
        return token;
    }
    public String createRoom(contestCreationReq contestCreationReq, Authentication authentication){
        try {
            ContestRoom contestRoom = roomService.createRoomOnlyRoom(contestCreationReq, authentication);
            if(contestRoom==null){
                return  "room is not created";
            }
            return contestRoom.getId();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Room is not created";
    }
    public UserProfile getUserProfile(Authentication authentication){
        String email=authentication.getName();
        return userProfileRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

}
