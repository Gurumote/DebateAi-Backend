package org.gam.dedatebackend.Controller;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Contest.ContestCreationReq;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Service.ContestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/ContestRoom")
@RequiredArgsConstructor
public class ContestRoomController {

    private final ContestService contestService;
    private  final ContestRepo contestRepo;
    @PostMapping("create-room")
    public ResponseEntity<?> createRoom(@RequestBody ContestCreationReq contestReq, Authentication authentication) {
       ContestRoom contestRoom= contestService.createRoom(contestReq,authentication);
       if(contestRoom==null){
           return ResponseEntity.badRequest().body("Error");
       }
       contestRepo.save(contestRoom);
       return ResponseEntity.ok().body(contestRoom);
    }
    @GetMapping("roomId/{id}")
    public Optional<ContestRoom> getRoomById(@PathVariable String id) {
        return contestRepo.findById(id);
    }
}
