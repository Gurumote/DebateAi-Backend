package org.gam.dedatebackend.Model.Contest;


import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ContestCreationReq {
    String roomName;
    private Long NumberOfParticipants;
    private String debateType;
    private Timestamp endTime;
}
