package org.gam.dedatebackend.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Enum.DebateType;
import org.gam.dedatebackend.Model.Contest.RoomParticipant;
import org.gam.dedatebackend.Model.Request.ContestCreationReq;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.RoomParticipantRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.gam.dedatebackend.Service.Componet.WebSocketRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.gam.dedatebackend.Model.Session.SessionInfo;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ContestService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();
    private final ContestRepo contestRepo;
    private final UserProfileRepo userProfileRepo;
    private final RoomParticipantService roomParticipantService;
    private final RoomParticipantRepo roomParticipantRepo;
    private final WebSocketRegistry webSocketRegistry;
    public ContestRoom createRoom(ContestCreationReq contestReq, Authentication authentication) {

        long userid=getUserid(authentication);
        ContestRoom room= ContestRoom.builder()
                .id(genrateString())
                .hostId(userid)
                .debateType(DebateType.valueOf(contestReq.getDebateType()))
                .NumberOfParticipants(contestReq.getNumberOfParticipants())
                .roomName(contestReq.getRoomName())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .endTime(contestReq.getEndTime())
                .list(new ArrayList<>())
                .participants(new ArrayList<>())
                .build();
         contestRepo.save(room);
        RoomParticipant participant=roomParticipantService.createJoinReq(room.getId(),authentication);
        roomParticipantRepo.save(participant);
        return room;
    }

    public void joinRoom(String roomId, String sessionId, Long userId) {
        ContestRoom room=contestRepo.findById(roomId).orElseThrow(()->new RuntimeException("Room Not Found"));
        boolean alreadyJoined=roomParticipantRepo.existsByContestRoom_IdAndUserIdAndLeftAtIsNull(roomId,userId);
        if(alreadyJoined){
            return;
        }
        RoomParticipant participant=roomParticipantService.createJoinReqByUserId(room.getId(),userId);
        roomParticipantRepo.save(participant);
        room.setNumberOfParticipants(roomParticipantRepo.countByContestRoom_IdAndLeftAtIsNull(roomId));
        contestRepo.save(room);
    }




    public void removeParticipationByHost(Long hostId,Long userId,String roomId){
        ContestRoom room = contestRepo.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        if(room.getHostId()!=hostId){
            throw new RuntimeException("Only host can remove participants");
        }
        if (hostId.equals(userId)) {
            throw new RuntimeException("Host cannot remove himself using this action");
        }
        RoomParticipant participant = roomParticipantRepo
                .findTopByContestRoom_IdAndUserIdAndLeftAtIsNullOrderByJoinedAtDesc(roomId, userId)
                .orElseThrow(() -> new RuntimeException("Target participant not active"));
        participant.setLeftAt(Timestamp.from(Instant.now()));
        roomParticipantRepo.save(participant);
        room.setNumberOfParticipants(roomParticipantRepo.countByContestRoom_IdAndLeftAtIsNull(roomId));
        contestRepo.save(room);
    }

    @Transactional
    public void handleDisconnect(String sessionId,Long hostId){
        SessionInfo info = webSocketRegistry.closeSession(sessionId);
        if (info == null) {
            return;
        }

        String roomId = info.getRoomId();
        Long userId = info.getUserId();
        Optional<ContestRoom> roomOpt = contestRepo.findById(roomId);
        if(roomOpt.isEmpty()){
            return;
        }
        ContestRoom room = roomOpt.get();
        Timestamp now = Timestamp.from(Instant.now());
        if(hostId!=room.getHostId()){
             throw new RuntimeException("Only host can End Session");
        }
        List<RoomParticipant> activeParticipants=room.getParticipants();
        for (RoomParticipant participant : activeParticipants) {
            participant.setLeftAt(now);
        }

        roomParticipantRepo.saveAll(activeParticipants);

        room.setEndTime(now);
        room.setNumberOfParticipants(0);
        contestRepo.save(room);
    }

    @Transactional
    public void leaveRoom(Long userId, String roomId) {
        ContestRoom room = contestRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getEndTime() != null) {
            throw new RuntimeException("Session already ended");
        }

        RoomParticipant participant = roomParticipantRepo
                .findTopByContestRoom_IdAndUserIdAndLeftAtIsNullOrderByJoinedAtDesc(roomId, userId)
                .orElseThrow(() -> new RuntimeException("User is not an active participant in this room"));

        participant.setLeftAt(Timestamp.from(Instant.now()));
        roomParticipantRepo.save(participant);

        long activeCount = roomParticipantRepo.countByContestRoom_IdAndLeftAtIsNull(roomId);
        room.setNumberOfParticipants(activeCount);
        contestRepo.save(room);

    }








    public long getUserid(Authentication authentication) {
        String email = authentication.getName();
        UserProfile user = userProfileRepo.findByemail(email);
        return user.getId();
    }
    public String genrateString(){
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    public ContestRoom getRoomId(String id){

       ContestRoom contestRoom = contestRepo.findById(id).get();
        return contestRoom;
    }


}
