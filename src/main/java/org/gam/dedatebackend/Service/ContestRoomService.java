package org.gam.dedatebackend.Service;


import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.DTO.Request.contestCreationReq;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.Contest.Participant.RoomParticipant;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Util.LiveKitUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestRoomService {
    private final roomService roomService;
    private final participantService participantService;
    private final LiveKitUtil liveKitUtil;
    private final ContestRepo contestRepo;
    public String createRoomAndGenerateTokenOfHost(contestCreationReq contestCreationReq, Authentication authentication){
        //Creating room in the DB
        ContestRoom contestRoom=roomService.createRoom(contestCreationReq, authentication);
        contestRoom.setSetLiveAt(Instant.now());
        contestRoom.setCurrentParticipantsSize(1);
        contestRepo.save(contestRoom);
        //adding Host in the Room(DB)
        RoomParticipant roomParticipant=participantService.addParticipant(contestRoom,authentication, Team.HOST);
        return liveKitUtil.generateToken(contestRoom,authentication.getName(),roomParticipant.getTeam());
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
}
