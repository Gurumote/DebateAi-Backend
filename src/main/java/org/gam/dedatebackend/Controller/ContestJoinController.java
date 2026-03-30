package org.gam.dedatebackend.Controller;


import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Contest.ContestJoinReq;
import org.gam.dedatebackend.Model.Contest.RoomParticipant;
import org.gam.dedatebackend.Repo.RoomParticipantRepo;
import org.gam.dedatebackend.Service.ContestJoinReqService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/ContestRoom")
@RequiredArgsConstructor
public class ContestJoinController {
    private final ContestJoinReqService contestJoinReqService;
    private final RoomParticipantRepo roomParticipantsRepo;
    @PostMapping("join-room")
    public ResponseEntity<?> joinRoom(@RequestBody ContestJoinReq contestJoinReq, Authentication authentication){

        RoomParticipant participant = contestJoinReqService.createJoinReq(contestJoinReq, authentication);
        roomParticipantsRepo.save(participant);
        return ResponseEntity.ok(participant);
    }
}
