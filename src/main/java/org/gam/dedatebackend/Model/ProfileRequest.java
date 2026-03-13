package org.gam.dedatebackend.Model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {
    private String Name;
    private String email;
    private String Password;
}
