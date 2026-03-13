package org.gam.dedatebackend.Model.Contest;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ContestReq {
    String roomname;
    private long Numberofparticipants;
    private String debatetype;
}
