package org.gam.dedatebackend.Service;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Model.Contest.ContestCreationReq;
import org.gam.dedatebackend.Model.Contest.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ContestService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();
    private final ContestRepo contestRepo;
    private final UserProfileRepo userProfileRepo;
    public ContestRoom createRoom(ContestCreationReq contestReq, Authentication authentication) {

        long userid=getUserid(authentication);
        return ContestRoom.builder()
                .id(genrateString())
                .hostId(userid)
                .debateType(contestReq.getDebateType())
                .NumberOfParticipants(contestReq.getNumberOfParticipants())
                .roomName(contestReq.getRoomName())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .endTime(contestReq.getEndTime())
                .list(new ArrayList<>())
                .build();
    }
    public long getUserid(Authentication authentication) {
        String email = authentication.getName();
        UserProfile user = userProfileRepo.findByemail(email);
        return user.getId();
    }
    public String genrateString(){
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
