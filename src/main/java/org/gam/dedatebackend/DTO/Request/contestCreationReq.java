package org.gam.dedatebackend.DTO.Request;


import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class contestCreationReq {
    String roomName;
    private Long teamSize;
    private String debateType;
    private Timestamp endTime;
}
