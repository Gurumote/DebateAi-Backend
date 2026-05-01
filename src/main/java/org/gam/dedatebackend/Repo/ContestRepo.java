package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContestRepo extends JpaRepository<ContestRoom,String> {
    @Query("SELECT r FROM ContestRoom r WHERE r.setLiveAt IS NOT NULL AND r.setLiveAt <= CURRENT_TIMESTAMP AND r.endTime > CURRENT_TIMESTAMP")
    List<ContestRoom> findActiveRooms();
}
