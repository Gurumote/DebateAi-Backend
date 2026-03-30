package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Model.Contest.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomParticipantRepo extends JpaRepository<RoomParticipant,Long> {
}
