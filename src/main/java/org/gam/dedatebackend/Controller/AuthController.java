package org.gam.dedatebackend.Controller;


import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.DTO.Request.AuthRequest;
import org.gam.dedatebackend.DTO.Response.AuthResponse;
import org.gam.dedatebackend.Service.ProfileDetailService;
import org.gam.dedatebackend.Util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final ProfileDetailService profileDetailService;
    private final JwtUtil jwtUtil;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest){
        try{
            authenticate(authRequest.getEmail(),authRequest.getPassword());
            final UserDetails userDetails=profileDetailService.loadUserByUsername(authRequest.getEmail());//user detail inbuilt class not user implemented
            final String token=jwtUtil.getToken(userDetails);
            ResponseCookie responseCookie=ResponseCookie.from("jwt",token)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(new AuthResponse(authRequest.getEmail(),token));
        }catch (BadCredentialsException e){
            Map<String,Object> error=new HashMap<>();
            error.put("error","Bad Credentials");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }catch (DisabledException e){
            Map<String,Object> error = new HashMap<>();
            error.put("error","User account is disabled");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
}
