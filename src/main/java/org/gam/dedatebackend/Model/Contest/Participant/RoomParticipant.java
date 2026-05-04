package org.gam.dedatebackend.Model.Contest.Participant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;

import java.time.Instant;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Builder
public class RoomParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;
    private Instant joinedAt;
    private Instant leftAt;
    @Enumerated(EnumType.STRING)
    private Team team;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference(value = "room-participants")
    private ContestRoom contestRoom;
}
