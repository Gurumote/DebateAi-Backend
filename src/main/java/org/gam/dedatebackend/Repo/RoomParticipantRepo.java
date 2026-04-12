package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Model.Contest.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface RoomParticipantRepo extends JpaRepository<RoomParticipant,Long> {
    Boolean existsByContestRoom_IdAndUserIdAndLeftAtIsNull(String roomId, Long userId);

    Optional<RoomParticipant> findTopByContestRoom_IdAndUserIdAndLeftAtIsNullOrderByJoinedAtDesc(String roomId, Long userId);

    long countByContestRoom_IdAndLeftAtIsNull(String roomId);

    List<RoomParticipant> findByContestRoom_IdAndLeftAtIsNull(String roomId);
}
