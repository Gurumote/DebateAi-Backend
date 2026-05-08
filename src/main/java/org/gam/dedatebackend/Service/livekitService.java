package org.gam.dedatebackend.Service;

import io.livekit.server.*;
import livekit.LivekitModels;
import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.DTO.Request.MessageRequest;
import org.gam.dedatebackend.Enum.roomStatus;
import org.gam.dedatebackend.Model.Contest.Participant.RoomParticipant;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.Contest.RoomMessages.MessagesOfRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.RoomParticipantRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.gam.dedatebackend.userDefinedExecption.LiveKitException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static io.micrometer.core.instrument.config.NamingConvention.identity;

@Service
@RequiredArgsConstructor
public class livekitService {

    private final RoomServiceClient roomServiceClient;
    private final UserProfileRepo userProfileRepo;
    private final ContestRepo contestRepo;
    private final messageService messageService;
    private final RoomParticipantRepo participantRepo;
    public void createLiveKitRoom(ContestRoom contestRoom){
        if (contestRoom == null || contestRoom.getId() == null || contestRoom.getId().isBlank()) {
            throw new IllegalArgumentException("Invalid contest room");
        }
        String roomName = contestRoom.getId();
        int maxParticipants = (int) (contestRoom.getTeamSize() * 2) + 1;
        try {
            Response<LivekitModels.Room> response = roomServiceClient.createRoom(
                    roomName,
                    300,                    // emptyTimeout: auto-delete room after 5 min of no participants
                    maxParticipants,        // maxParticipants: enforce your team size
                    null,                   // nodeId: let LiveKit pick the best node
                    contestRoom.getDebateType().toString(),  // metadata: store debate type for later queries
                    50,                     // minPlayoutDelay: 50ms minimum latency
                    200,                    // maxPlayoutDelay: 200ms maximum (adjust based on your network)
                    true,                   // syncStreams: sync audio/video across participants (you want this)
                    60                      // departureTimeout: 60-second grace period before removing participant
            ).execute();

            if (!response.isSuccessful()) {
                throw new LiveKitException("U fucked up");
            }

        } catch (IOException e) {
            throw new LiveKitException("Server is fucked up");
        }
    }
    public void removeParticipant(String roomId, String participantId, Authentication authentication){
        UserProfile userProfile = getUserProfile(authentication);
        long hostUserID = userProfile.getId();
        ContestRoom room=contestRepo.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        long hostId=room.getHost().getId();
        UserProfile p1 = userProfileRepo.findById(Long.valueOf(participantId)).orElseThrow(() -> new RuntimeException("Participant not found: " + participantId));
        if(hostId!=hostUserID){
            System.out.println("You don't have access to remove Participant from the room");
        }else{
            try {
                long currentParticipant=room.getCurrentParticipantsSize();
                room.setCurrentParticipantsSize(currentParticipant-1);
                contestRepo.save(room);
                Response<Void> response = roomServiceClient.removeParticipant(roomId, participantId).execute();
                if (!response.isSuccessful()) {
                    throw new LiveKitException("Failed to remove participant: " + response.code());
                }
            }catch (Exception e){
                System.out.println("You are not able to Participant from the room");
            }
        }

    }
    public void deleteRoom(ContestRoom room, Authentication authentication){
        UserProfile userProfile = getUserProfile(authentication);
        String roomId=room.getId();
        long hostUserID = userProfile.getId();
        long hostId=room.getHost().getId();
        if(hostId!=hostUserID){
            throw new LiveKitException("You don't have access to delete room");
        }else{
            try{
                roomServiceClient.deleteRoom(roomId);
                room.setEndTime(Instant.now());
                room.setRoomStatus(roomStatus.DEAD);
                contestRepo.save(room);
            }catch (LiveKitException e){
                throw new LiveKitException("Failed to delete room");
            }
        }
        Instant endTime=room.getEndTime();
        Instant time=Instant.now();
        if(endTime.isBefore(time)){
            throw new LiveKitException("Room is Still Active and U don't Have Access to Delete Room");
        }else{
            try{
                roomServiceClient.deleteRoom(roomId);
                room.setEndTime(Instant.now());
                room.setRoomStatus(roomStatus.DEAD);
                contestRepo.save(room);
            }catch (LiveKitException e){
                throw new LiveKitException("Failed to delete room");
            }
        }
    }
    public void sendChatMessage(MessageRequest messageRequest, Authentication authentication){
        try{
            MessagesOfRoom msg = messageService.sendMessage(messageRequest,authentication)
                    .orElseThrow(() -> new RuntimeException("Message not created"));

            UserProfile sender = msg.getUser();
            String message = msg.getContent();
            String roomId = msg.getContestRoom().getId();

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", sender.getUsername());
            payload.put("message", message);
            payload.put("roomId", roomId);
            payload.put("timestamp", System.currentTimeMillis());

            ObjectMapper mapper = new ObjectMapper();
            byte[] data = mapper.writeValueAsBytes(payload);
            Response<Void> response = roomServiceClient.sendData(
                    roomId,
                    data,
                    LivekitModels.DataPacket.Kind.RELIABLE,
                    new ArrayList<>(),
                    new ArrayList<>()
            ).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("LiveKit send failed");
            }
        }catch (LiveKitException e){
            throw new LiveKitException("Failed to send chat message");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<LivekitModels.ParticipantInfo> listOfParticipants(String roomId){
        try{
            Call<List<LivekitModels.ParticipantInfo>> call =
                    roomServiceClient.listParticipants(roomId);
            Response<List<LivekitModels.ParticipantInfo>> response = call.execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed: " + response.code());
            }
            return response.body();
        }catch (LiveKitException e){
            throw new LiveKitException("Failed to list participants");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void checkRooms() {
        try {
            // 1. You MUST call .execute() to actually send the request to LiveKit Cloud
            // This returns a Retrofit Response object
            Response<List<LivekitModels.Room>> response = roomServiceClient.listRooms(null).execute();

            // 2. Check if the request was successful (HTTP 200)
            if (response.isSuccessful() && response.body() != null) {
                List<LivekitModels.Room> rooms = response.body();

                if (rooms.isEmpty()) {
                    throw new LiveKitException("No rooms found");
                } else {
                    for (LivekitModels.Room room : rooms) {
                        System.out.println("Active Room Name: " + room.getName());
                        System.out.println("Participant Count: " + room.getNumParticipants());
                    }
                }
            } else {
                System.err.println("Failed to fetch rooms. Error code: " + response.code());
            }
        } catch (IOException e) {
            // Handle network errors (e.g., host unreachable)
            System.err.println("Network error while checking rooms: " + e.getMessage());
        }
    }


//    public ResponseEntity<String> muteRoom(String roomId, String participantId, Authentication authentication) throws IOException {
//        UserProfile userProfile = getUserProfile(authentication);
//        RoomParticipant participant=
//        String identity="";
//        ContestRoom room=contestRepo.findById(roomId).orElseThrow(() -> new LiveKitException("Room not found"));
//        if(room.getHost().getId()!=userProfile.getId()){
//            throw new LiveKitException("You are not able to mute room");
//        }
//        List<LivekitModels.ParticipantInfo> participants =
//                roomServiceClient.listParticipants(roomId)
//                        .execute()
//                        .body();
//
//        LivekitModels.ParticipantInfo participant = participants.stream()
//                .filter(p -> p.getIdentity().equals(identity))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        LivekitModels.TrackInfo audioTrack = participant.getTracksList().stream()
//                .filter(t -> t.getType().name().equals("AUDIO"))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Audio track not found"));
//
//        roomServiceClient.mutePublishedTrack(
//                roomId,
//                participantId,
//                audioTrack.getSid(),
//                true
//        ).execute();
//        return  ResponseEntity.ok().build();
//    }
    public UserProfile getUserProfile(Authentication authentication){
        String email=authentication.getName();
        return userProfileRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}