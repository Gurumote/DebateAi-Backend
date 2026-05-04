package org.gam.dedatebackend.Model.Contest.Room;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.gam.dedatebackend.Enum.DebateType;
import org.gam.dedatebackend.Enum.roomStatus;
import org.gam.dedatebackend.Model.Contest.RoomMessages.MessagesOfRoom;
import org.gam.dedatebackend.Model.Contest.Participant.RoomParticipant;
import org.gam.dedatebackend.Model.UserProfile;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ContestRoom {
    @Id
    private String id;
    @Column(nullable = false)
    private String roomName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private UserProfile host;
    private long teamSize;
    private long totalParticipantsSize;
    @Column(name="current_Participants_size")
    private long currentParticipantsSize=0;
    @CurrentTimestamp
    @Column(updatable = false)
    private Instant createdAt;
    private Instant setLiveAt;
    private Instant endTime;
    @Enumerated(EnumType.STRING)
    private DebateType debateType;
    @Enumerated(EnumType.STRING)
    private roomStatus roomStatus;
    @JsonManagedReference
    @OneToMany(mappedBy = "contestRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default//it preserves the actual state of the field when it was build when we do not use Builder then messages =null when we use message=[]
    private List<MessagesOfRoom> list=new ArrayList<>();
    @OneToMany(mappedBy = "contestRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "room-participants")
    @Builder.Default
    private List<RoomParticipant> participants = new ArrayList<>();
}
