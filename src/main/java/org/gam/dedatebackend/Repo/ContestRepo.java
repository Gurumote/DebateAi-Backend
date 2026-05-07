package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Enum.roomStatus;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContestRepo extends JpaRepository<ContestRoom,String> {
    @Query("SELECT r FROM ContestRoom r WHERE r.setLiveAt IS NOT NULL AND r.setLiveAt <= CURRENT_TIMESTAMP AND r.endTime > CURRENT_TIMESTAMP")
    List<ContestRoom> findActiveRooms();
    @Query("""
SELECT r FROM ContestRoom r
WHERE (r.roomStatus = 'INITIALIZED' OR r.roomStatus = 'LIVE')
AND r.endTime > CURRENT_TIMESTAMP
""")
    List<ContestRoom> findInitializedOrLiveRooms();

    List<ContestRoom> findByRoomStatusAndHost_Id(roomStatus status, Long hostId);
    @Query("""
    SELECT DISTINCT r
    FROM ContestRoom r
    LEFT JOIN r.participants p
    WHERE r.host.id = :userId
       OR (
            p.user.id = :userId
            AND p.team <> org.gam.dedatebackend.Enum.Team.AUDIENCE
          )
""")
    List<ContestRoom> findRoomsWhereUserIsHostOrTeam(
            @Param("userId") Long userId
    );
}
