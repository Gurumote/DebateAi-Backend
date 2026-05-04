package org.gam.dedatebackend.Service;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.DTO.Request.MessageRequest;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.Contest.RoomMessages.MessagesOfRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.gam.dedatebackend.Repo.ContestRepo;
import org.gam.dedatebackend.Repo.UserProfileRepo;
import org.gam.dedatebackend.Repo.messageRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class messageService {
    private final messageRepo messageRepo;
    private final ContestRepo contestRepo;
    private final UserProfileRepo userProfileRepo;
    public Optional<MessagesOfRoom> sendMessage(MessageRequest messageRequest, Authentication authentication){
        String roomId=messageRequest.getRoomId();
        ContestRoom contestRoom=contestRepo.findById(roomId).orElseThrow(()->new RuntimeException("Room Not Found"));
        UserProfile userProfile=getUser(authentication);
        MessagesOfRoom messages=MessagesOfRoom.builder()
                .user(userProfile)
                .content(messageRequest.getContent())
                .sentAt(Instant.now())
                .contestRoom(contestRoom).build();
        messageRepo.save(messages);
        return Optional.of(messages);
    }
    public UserProfile getUser(Authentication authentication){
        String username=authentication.getName();
        return userProfileRepo.findByEmail(username).orElseThrow(()->new RuntimeException("User Not Found"));
    }
}
