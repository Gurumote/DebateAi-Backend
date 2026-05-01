package org.gam.dedatebackend.DTO.Request;


import lombok.*;
import org.gam.dedatebackend.Enum.DebateType;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class contestCreationReq {
    String roomName;
    private Long teamSize;
    private DebateType debateType;
    private Instant endTime;
}