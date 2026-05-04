package org.gam.dedatebackend.Model.Contest.ContestVotes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;

@AllArgsConstructor
@Data
@Entity
@NoArgsConstructor
@Builder
public class ContestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Team WinnerTeam;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference(value = "room-messages")
    private ContestRoom contestRoom;
}
