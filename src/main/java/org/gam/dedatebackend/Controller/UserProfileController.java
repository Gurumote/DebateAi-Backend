package org.gam.dedatebackend.Controller;


import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/UserProfile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("{id}")
    public UserProfile findById(@PathVariable long id) {
        return userProfileService.findbyId(id);
    }
    @GetMapping("/all")
    public List<UserProfile>  findAll(){
        return userProfileService.findALl();
    }
    @PostMapping
    public UserProfile create(@RequestBody UserProfile userProfile){
        return userProfileService.createprofile(userProfile);
    }

}
