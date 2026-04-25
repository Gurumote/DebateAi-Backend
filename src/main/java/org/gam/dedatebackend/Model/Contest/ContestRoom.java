package org.gam.dedatebackend.Model.Contest;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.gam.dedatebackend.Enum.DebateType;
import org.gam.dedatebackend.Model.UserProfile;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Timestamp;
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
    private long teamSize;//important factor
    private long totalParticipants;
    @CurrentTimestamp
    @Column(updatable = false)
    private Instant createdAt;
    private Timestamp endTime;
    private DebateType debateType;
    @JsonManagedReference
    @OneToMany(mappedBy = "contestRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default//it preserves the actual state of the field when it was build when we do not use Builder then messages =null when we use message=[]
    private List<MessagesOfRoom> list=new ArrayList<>();
    @OneToMany(mappedBy = "contestRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "room-participants")
    @Builder.Default
    private List<RoomParticipant> participants = new ArrayList<>();
}
