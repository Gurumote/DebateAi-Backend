package org.gam.dedatebackend.DTO.Request;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter@Setter
public class AuthRequest {
    private String email;
    private String password;
}
