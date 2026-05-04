package org.gam.dedatebackend.Controller;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Enum.Team;

import org.gam.dedatebackend.Model.Contest.ContestVotes.ContestResult;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.contestResultRepo;
import org.gam.dedatebackend.Repo.contestVotesRepo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/result")
@RequiredArgsConstructor
public class resultController {
    private final contestVotesRepo contestVotesRepo;
    private final ContestRepo contestRepo;
    private final contestResultRepo contestResultRepo;
    @PostMapping("/{roomId}/result")
    public ContestResult result(@PathVariable String roomId) {
        ContestRoom room=contestRepo.findById(roomId).orElseThrow(()->new RuntimeException("Room not found: " + roomId));
        long teamACount = contestVotesRepo.countByContestRoom_IdAndTeam(roomId, Team.RED);
        long teamBCount = contestVotesRepo.countByContestRoom_IdAndTeam(roomId, Team.BLUE);
        ContestResult result= ContestResult.builder()
                .contestRoom(room)
                .WinnerTeam((teamACount>teamBCount) ? Team.RED : Team.BLUE)
                .build();
        return contestResultRepo.save(result);
    }
}