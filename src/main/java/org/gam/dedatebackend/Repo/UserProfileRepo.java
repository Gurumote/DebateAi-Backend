package org.gam.dedatebackend.Repo;

import org.gam.dedatebackend.Model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {
//    UserProfile findByUsername(String username);
    UserProfile findByemail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String name);
    Optional<UserProfile> findByUsername(String username);
    Optional<UserProfile> findByEmail(String email);
}
