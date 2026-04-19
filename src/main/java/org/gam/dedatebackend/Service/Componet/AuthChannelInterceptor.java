package org.gam.dedatebackend.Service.Componet;

import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Util.JwtUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.security.Principal;
import java.util.Collections;


@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;


    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor =MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        System.out.println("COMMAND = " + command);

        if (StompCommand.CONNECT.equals(command)) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            System.out.println("AUTH HEADER = " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            Authentication authentication =new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());

            accessor.setUser(authentication);

            System.out.println("AUTHENTICATION SET = " + authentication.getName());
        } else {
            Principal user = accessor.getUser();
            System.out.println("USER FROM SESSION = " + user);
        }

        return message;
    }
}