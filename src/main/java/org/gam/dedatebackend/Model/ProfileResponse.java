package org.gam.dedatebackend.Model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private String userid;
    private String username;
    private String Email;
    private boolean isActive;
}
