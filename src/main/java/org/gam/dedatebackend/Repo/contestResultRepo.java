package org.gam.dedatebackend.Repo;


import org.gam.dedatebackend.Model.Contest.ContestVotes.ContestResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface contestResultRepo extends JpaRepository<ContestResult,Long> {
}
