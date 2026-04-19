package org.gam.dedatebackend.Controller;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Request.ContestCreationReq;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.gam.dedatebackend.Service.Componet.WebSocketRegistry;
import org.gam.dedatebackend.Service.ContestService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ContestRoomController {

    private final ContestService contestService;
    private final UserProfileRepo userProfileRepo;
    private final WebSocketRegistry webSocketRegistry;

    @MessageMapping("room/create-room")
    public ResponseEntity<?> createRoom(@Payload ContestCreationReq contestReq, Authentication authentication, @Header("simpSessionId") String sessionId) {
        try {
            ContestRoom contestRoom = contestService.createRoom(contestReq, authentication);
            webSocketRegistry.connectSession(sessionId, contestRoom.getId(), contestRoom.getHostId());
            return ResponseEntity.ok().body(contestRoom);
        } catch (Exception e) {
            System.err.println("Error creating room: " + e.getMessage());
            return ResponseEntity.badRequest().body("Could not create room: " + e.getMessage());
        }
    }
    @MessageMapping("room/join/{id}")
    public void joinRoom(@PathVariable String roomId, @Header("simpleSessionId")String sessionId,Authentication authentication ){
        Long userId=getUserid(authentication);
        contestService.joinRoom(roomId,sessionId,userId);
        webSocketRegistry.connectSession(sessionId,roomId,contestService.getUserid(authentication));
    }

    @MessageMapping("room/leaveRoom/{id}")
    public void leaveRoom(@PathVariable String roomId, Long userId){
        contestService.leaveRoom(userId,roomId);
    }

    @MessageMapping("room/removeUser")
    public void removeUser(Long userId,Long hostId,String roomId){
        contestService.removeParticipationByHost(hostId,userId,roomId);
    }
    public long getUserid(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Authentication is null");
        }

        String email = authentication.getName();
        System.out.println("EMAIL FROM AUTH = " + email);

        UserProfile user = userProfileRepo.findByemail(email);
        System.out.println("USER FROM DB = " + user);

        if (user == null) {
            throw new RuntimeException("User not found for email: " + email);
        }

        return user.getId();
    }
}
