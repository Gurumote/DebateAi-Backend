package org.gam.dedatebackend.Model.Contest;


import jakarta.persistence.*;
import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ContestJoinReq {
    private String roomId;
}
