package org.gam.dedatebackend.Service;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Contest.ContestReq;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepo contestRepo;
    private final UserProfileRepo userProfileRepo;
    public ContestRoom createRoom(ContestReq contestReq, Authentication authentication) {

        long userid=getuserid(authentication);
        return ContestRoom.builder()
                .id(userid)
                .debateType(contestReq.getDebatetype())
                .NumberOfParticipants(contestReq.getNumberofparticipants())
                .roomName(contestReq.getRoomname())
                .build();
    }
    public long getuserid(Authentication authentication) {
        String email = authentication.getName();
        UserProfile user = userProfileRepo.findByemail(email);
        return user.getId();
    }
}
