package org.gam.dedatebackend.Model.Contest;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MessagesOfRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String Sender;
    private String content;
    private LocalDateTime localDateTime;
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "room_id")
    private ContestRoom contestRoom;
}