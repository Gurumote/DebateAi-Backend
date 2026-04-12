package org.gam.dedatebackend.Controller;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Request.ContestCreationReq;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.RoomParticipantRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.gam.dedatebackend.Service.Componet.WebSocketRegistry;
import org.gam.dedatebackend.Service.ContestService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ContestRoomController {

    private final ContestService contestService;
    private  final ContestRepo contestRepo;
    private final UserProfileRepo userProfileRepo;
    private final RoomParticipantRepo roomParticipantRepo;
    private final WebSocketRegistry webSocketRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;
    @MessageMapping("room/create-room")
    public ResponseEntity<?> createRoom(@RequestBody ContestCreationReq contestReq, Authentication authentication,String sessionId) {
       ContestRoom contestRoom= contestService.createRoom(contestReq,authentication);
       if(contestRoom==null){
           return ResponseEntity.badRequest().body("Error");
       }
       contestRepo.save(contestRoom);
       webSocketRegistry.connectSession(sessionId,contestRoom.getId(),contestService.getUserid(authentication));
       return ResponseEntity.ok().body(contestRoom);
    }
    @MessageMapping("room/join/{id}")
    public void joinRoom(@PathVariable String roomId, @Header("simpleSessionId")String sessionId,Authentication authentication ){
        Long userId=getUserId(authentication);
        contestService.joinRoom(roomId,sessionId,userId);
            webSocketRegistry.connectSession(sessionId,roomId,contestService.getUserid(authentication));
    }





//    private void publishRoomEvent(String roomId, RoomEvent event) {
//        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, event);
//    }






    public long getUserId(Authentication authentication) {
        String email = authentication.getName();
        UserProfile user = userProfileRepo.findByemail(email);
        return user.getId();
    }
//    @GetMapping("roomId/{id}")
//    public Optional<ContestRoom> getRoomById(@PathVariable String id) {
//        return contestRepo.findById(id);
//    }

}
