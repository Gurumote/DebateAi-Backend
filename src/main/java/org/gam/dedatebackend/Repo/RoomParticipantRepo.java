package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.Participant.RoomParticipant;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface RoomParticipantRepo extends JpaRepository<RoomParticipant,Long> {
    Boolean existsByContestRoom_IdAndUser_IdAndLeftAtIsNull(String roomId, Long userId);

    Optional<RoomParticipant> findTopByContestRoom_IdAndUser_IdAndLeftAtIsNullOrderByJoinedAtDesc(
            String roomId,
            Long userId
    );

    long countByContestRoom_IdAndLeftAtIsNull(String roomId);

    List<RoomParticipant> findByContestRoom_IdAndLeftAtIsNull(String roomId);

    List<RoomParticipant> findByUserAndContestRoom(UserProfile user, ContestRoom contestRoom);
    Optional<RoomParticipant> findByUser_Id(Long userId);
    List<RoomParticipant> findByContestRoom(ContestRoom contestRoom);
    long countByContestRoomAndTeam(ContestRoom room, Team team);
}
