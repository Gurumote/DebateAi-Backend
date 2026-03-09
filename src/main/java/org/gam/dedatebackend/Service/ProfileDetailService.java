package org.gam.dedatebackend.Service;

import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ProfileDetailService implements UserDetailsService {
    @Autowired
    UserProfileRepo userProfileRepo;
    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        UserProfile user=userProfileRepo.findByemail(email);
        return new User(user.getEmail(), user.getPassword(),new ArrayList<>());
    }
}
