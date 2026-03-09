package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {
    UserProfile findByUsername(String username);
    UserProfile findByemail(String email);
}
