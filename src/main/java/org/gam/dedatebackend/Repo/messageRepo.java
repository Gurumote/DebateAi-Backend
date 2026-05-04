package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Model.Contest.RoomMessages.MessagesOfRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface messageRepo extends JpaRepository<MessagesOfRoom, Long> {

}
