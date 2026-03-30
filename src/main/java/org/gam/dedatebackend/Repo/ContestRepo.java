package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepo extends JpaRepository<ContestRoom,String> {
}
