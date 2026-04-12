package org.gam.dedatebackend.Controller;


import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Contest.RoomParticipant;
import org.gam.dedatebackend.Repo.RoomParticipantRepo;
import org.gam.dedatebackend.Service.RoomParticipantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/contestRoom")
@RequiredArgsConstructor
public class RoomParticipantController {
    private final RoomParticipantService roomParticipantService;
    private final RoomParticipantRepo roomParticipantsRepo;
    @PostMapping("join-room/{id}")
    public ResponseEntity<?> joinRoom(@PathVariable String id, Authentication authentication){

        RoomParticipant participant = roomParticipantService.createJoinReq(id, authentication);
        roomParticipantsRepo.save(participant);
        return ResponseEntity.ok(participant);
    }
}
