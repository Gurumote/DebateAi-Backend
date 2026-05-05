package org.gam.dedatebackend.Controller;

import livekit.LivekitModels;
import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.DTO.Request.MessageRequest;
import org.gam.dedatebackend.DTO.Request.contestCreationReq;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Enum.roomStatus;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.Contest.Participant.RoomParticipant;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.RoomParticipantRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.gam.dedatebackend.Service.ContestRoomService;
import org.gam.dedatebackend.Service.livekitService;
import org.gam.dedatebackend.Service.participantService;
import org.gam.dedatebackend.Util.LiveKitUtil;
import org.gam.dedatebackend.userDefinedExecption.LiveKitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;


@RestController
@RequestMapping("api/room")
@RequiredArgsConstructor
public class ContestRoomController {
    private final ContestRoomService contestRoomService;
    private final LiveKitUtil liveKitUtil;
    private final ContestRepo  contestRepo;
    private final UserProfileRepo userProfileRepo;
    private final livekitService livekitService;
    private final participantService participantService;
    private final RoomParticipantRepo participantRepo;

    @PostMapping("/createRoomAndJoin")
    public String createRoomAndJoin(@RequestBody contestCreationReq contestCreationReq, Authentication authentication){
        return contestRoomService.createRoomAndGenerateTokenOfHost(contestCreationReq, authentication);
    }
    @PostMapping("/createRoom")
    public String createRoom(@RequestBody contestCreationReq contestCreationReq, Authentication authentication){
        return contestRoomService.createRoom(contestCreationReq, authentication);
    }
    @PostMapping("/{roomId}/activateRoom")
    public ResponseEntity<String> activateRoom(@PathVariable String roomId,Authentication authentication){
            ContestRoom room=contestRepo.findById(roomId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
            UserProfile profile=getUser(authentication);
            if(profile.getId()!=room.getHost().getId()){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (room.getRoomStatus() != roomStatus.INITIALIZED) {
                return ResponseEntity.badRequest().body("Room cannot be activated");
            }
            if(room.getSetLiveAt()!=null){
                return ResponseEntity.ok("Room already activated");
            }
            try {
                livekitService.createLiveKitRoom(room);
                room.setSetLiveAt(Instant.now());
                room.setRoomStatus(roomStatus.LIVE);
                contestRepo.save(room);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "LiveKit failed");
            }
            return ResponseEntity.ok("Room activated");
        }
    @PostMapping("/{roomId}/token")
    public ResponseEntity<String> getToken(@PathVariable String roomId, Authentication authentication,@RequestParam Team team) {
        UserProfile user =getUser(authentication);
        long userName=user.getId();
        ContestRoom room = contestRepo.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        if(room.getSetLiveAt()==null){
            if(room.getHost().getId()==user.getId()){
                room.setSetLiveAt(Instant.now());
                livekitService.createLiveKitRoom(room);
                room.setCurrentParticipantsSize(1);
                contestRepo.save(room);
                String token = liveKitUtil.generateToken(room,  String.valueOf(userName),Team.HOST);
                RoomParticipant roomParticipant=participantService.addParticipant(room,authentication,Team.HOST);
                return ResponseEntity.ok(token);
            }else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is not Started yet");
            }
        }
        long redCount = participantRepo.countByContestRoomAndTeam(room, Team.RED);
        long blueCount = participantRepo.countByContestRoomAndTeam(room, Team.BLUE);
        long maxSize = room.getTeamSize();
        Team finalTeam;
        if (team == Team.RED) {
            if (redCount < maxSize) {
                finalTeam = Team.RED;
            } else if (blueCount < maxSize) {
                finalTeam = Team.BLUE;
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is full");
            }
        } else {
            if (blueCount < maxSize) {
                finalTeam = Team.BLUE;
            } else if (redCount < maxSize) {
                finalTeam = Team.RED;
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is full");
            }
        }
        long currentParticipant=room.getCurrentParticipantsSize();
        if(currentParticipant>= room.getTotalParticipantsSize()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Room is Full You are not Allowed to Enter");
        }
        Instant endTime=room.getEndTime();
        Instant time=Instant.now();
        if (endTime.isAfter(time)) {
            boolean alreadyActive = participantRepo.existsByContestRoom_IdAndUser_Id(room.getId(), user.getId());
            String token = liveKitUtil.generateToken(room, String.valueOf(userName), finalTeam);
            if (alreadyActive) {
                RoomParticipant participant=participantRepo.findByContestRoom_IdAndUser_Id(roomId,user.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
                participant.setJoinedAt(Instant.now());
                participant.setLeftAt(null);
                participant.setTeam(finalTeam);
                participantRepo.save(participant);
            }else{
                participantService.addParticipant(room, authentication, finalTeam);
            }
            return ResponseEntity.ok(token);
        }else{
            List<RoomParticipant> remaining =participantRepo.findByContestRoom_IdAndLeftAtIsNull(roomId);
            if(!remaining.isEmpty()){
                throw new LiveKitException("Room is Still Active");
            }else {
                livekitService.deleteRoom(room, authentication);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Room has already ended");
    }
    @GetMapping("/{roomId}")
    public ResponseEntity<ContestRoom> getRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(
                contestRepo.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"))
        );
    }
    @PostMapping("/{roomId}/tokenForAuidence")
    public ResponseEntity<String> getTokenForAudience(@PathVariable String roomId, Authentication authentication) {
        UserProfile user =getUser(authentication);
        String userName=user.getUsername();
        ContestRoom room = contestRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Instant endTime=room.getEndTime();
        Instant time=Instant.now();
        if (endTime.isAfter(time)) {
            boolean alreadyActive = participantRepo.existsByContestRoom_IdAndUser_Id(room.getId(), user.getId());
            String token = liveKitUtil.generateTokenForAudience(room, userName,Team.AUDIENCE);
            if (alreadyActive) {
                RoomParticipant participant=participantRepo.findByContestRoom_IdAndUser_Id(roomId,user.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
                participant.setJoinedAt(Instant.now());
                participant.setLeftAt(null);
                participant.setTeam(Team.AUDIENCE);
                participantRepo.save(participant);
            }else{
                participantService.addParticipant(room, authentication, Team.AUDIENCE);
            }
            return ResponseEntity.ok(token);
        }else{
            List<RoomParticipant> remaining =participantRepo.findByContestRoom_IdAndLeftAtIsNull(roomId);
            if(!remaining.isEmpty()){
                throw new LiveKitException("Room is Still Active");
            }else {
                livekitService.deleteRoom(room, authentication);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Room has already ended");
    }
    @PostMapping("{roomId}/{participantId}/removeParticipant")
    public ResponseEntity<String> removeParticipant(@PathVariable String roomId,@PathVariable String participantId, Authentication authentication) {
        livekitService.removeParticipant(roomId,participantId,authentication);
        return ResponseEntity.ok("Participant has been removed");
    }
    @PostMapping("/{roomId}/delete")
    public ResponseEntity<String> deleteRoom(@PathVariable String roomId, Authentication authentication){
        UserProfile user =getUser(authentication);
        ContestRoom room = contestRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        if(user.getId()!=room.getHost().getId()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("U fucker u are not allowed to delete this room");
        }
        livekitService.deleteRoom(room,authentication);
        return  ResponseEntity.ok("Room has been deleted");
    }
    @PostMapping("/{roomId}/chat")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest,Authentication authentication){
        livekitService.sendChatMessage(messageRequest,authentication);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/allRooms")
    public List<ContestRoom> getAllRooms(){
        return contestRepo.findInitializedOrLiveRooms();
    }
    @GetMapping("/userInitlizedRoom")
    public List<ContestRoom> getUserRooms(Authentication authentication){
        UserProfile user =getUser(authentication);
        return contestRepo.findByRoomStatusAndHost_Id(roomStatus.INITIALIZED,user.getId());
    }
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable String roomId, Authentication authentication) {
        UserProfile user = getUser(authentication);
        ContestRoom room = contestRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        long currentParticipant=room.getCurrentParticipantsSize();
        room.setCurrentParticipantsSize(currentParticipant>0?currentParticipant-1:0);
        List<RoomParticipant> participant = participantRepo.findByUserAndContestRoom(user, room);
        for(RoomParticipant p:participant){
            p.setLeftAt(Instant.now());
        }
        participantRepo.saveAll(participant);
        // HOST LEAVES
        if (user.getId()==room.getHost().getId()) {
            List<RoomParticipant> remaining =participantRepo.findByContestRoom_IdAndLeftAtIsNull(roomId);
            if (remaining.isEmpty()) {
                room.setEndTime(Instant.now());
                contestRepo.save(room);
                return ResponseEntity.ok("Room ended (no participants left)");
            } else {
                RoomParticipant newHost = remaining.getFirst();
                room.setHost(newHost.getUser());
                contestRepo.save(room);
                return ResponseEntity.ok("Room ended (host left)");
            }
        }
        contestRepo.save(room);
        return ResponseEntity.ok("Left room successfully");
    }
    @PostMapping("/allParticiapnt")
    public List<LivekitModels.ParticipantInfo> getAllParticipants(String roomId){
     return livekitService.listOfParticipants(roomId);
    }
//    @PostMapping("/{roomId}/mute")
//    public ResponseEntity<String> muteRoom(@PathVariable String roomId, String participantId,Authentication authentication) throws IOException {
////        RoomParticipant roomParticipant=RoomParticipantRepo.
//        return livekitService.muteRoom(roomId,participantId,authentication);
//    }
    private UserProfile getUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authentication");
        }
        String username = authentication.getName(); // safest way
        return userProfileRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}