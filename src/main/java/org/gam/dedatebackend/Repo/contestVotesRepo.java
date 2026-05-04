package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.ContestVotes.ContestVotes;
import org.gam.dedatebackend.Model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface contestVotesRepo extends JpaRepository<ContestVotes,Long> {
    @Query("SELECT v.user FROM ContestVotes v WHERE v.contestRoom.id = :roomId")
    Set<UserProfile> findUsersByRoomId(Long roomId);

    boolean existsByUserIdAndContestRoomId(Long userId, String roomId);

    List<ContestVotes> findByContestRoom_Id(String roomId);

    long countByContestRoom_IdAndTeam(String roomId, Team team);
}
