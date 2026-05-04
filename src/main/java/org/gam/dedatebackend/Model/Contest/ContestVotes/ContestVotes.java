package org.gam.dedatebackend.Model.Contest.ContestVotes;

import jakarta.persistence.*;
import lombok.*;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.gam.dedatebackend.Model.UserProfile;
import org.springframework.stereotype.Service;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter@Service
public class ContestVotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ContestRoom contestRoom;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Team team;
}
