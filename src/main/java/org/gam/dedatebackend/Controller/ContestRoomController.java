package org.gam.dedatebackend.Controller;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Contest.ContestReq;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> createRoom(@RequestBody ContestReq contestReq, Authentication authentication) {
       ContestRoom contestRoom= contestService.createRoom(contestReq,authentication);
       return ResponseEntity.ok().body(contestRoom);
    }
    @GetMapping("roomid/{id}")
    public Optional<ContestRoom> getRoomById(@PathVariable long id) {
        return contestRepo.findById(id);
    }
}
