package org.gam.dedatebackend.Service;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.Contest.ContestVotes.ContestVotes;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.contestVotesRepo;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class votesService {
    private final contestVotesRepo contestVotesRepo;
    public ContestVotes votes(ContestRoom room,UserProfile userProfile, Team team){
        boolean alreadyVoted = contestVotesRepo.existsByUserIdAndContestRoomId(userProfile.getId(), room.getId());

        if (alreadyVoted) {
            throw new RuntimeException("User already voted in this debate");
        }
        ContestVotes votes= ContestVotes.builder()
                .user(userProfile)
                .contestRoom(room)
                .team(team)
                .build();
        try {
            return contestVotesRepo.save(votes);
        } catch (Exception e) {
            throw new RuntimeException("Duplicate vote detected");
        }
    }
}
