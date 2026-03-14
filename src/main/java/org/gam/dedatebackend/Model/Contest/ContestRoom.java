package org.gam.dedatebackend.Model.Contest;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class ContestRoom {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String roomName;
    @Column(nullable = false)
    private long hostId;
    private long NumberOfParticipants;
    @CurrentTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
    private Timestamp endTime;
    private String debateType;
}
