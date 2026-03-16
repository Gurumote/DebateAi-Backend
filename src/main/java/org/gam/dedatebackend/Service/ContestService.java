package org.gam.dedatebackend.Service;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Contest.ContestReq;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepo contestRepo;
    private final UserProfileRepo userProfileRepo;
    public ContestRoom createRoom(ContestReq contestReq, Authentication authentication) {

        long userid=getuserid(authentication);
        return ContestRoom.builder()
                .hostId(userid)
                .debateType(contestReq.getDebateType())
                .NumberOfParticipants(contestReq.getNumberOfParticipants())
                .roomName(contestReq.getRoomName())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .endTime(contestReq.getEndTime())
                .list(new ArrayList<>(null))
                .build();
    }
    public long getuserid(Authentication authentication) {
        String email = authentication.getName();
        UserProfile user = userProfileRepo.findByemail(email);
        return user.getId();
    }
}
