package org.gam.dedatebackend.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.gam.dedatebackend.Service.ProfileDetailService;
import org.gam.dedatebackend.Util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final ProfileDetailService profileDetailService;

    public List<String> list=List.of("/api/login","/api/register");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path=request.getServletPath();
        if(list.contains(path)){
            filterChain.doFilter(request, response);
            return;
        }
        String jwt=null;
        String email=null;
        //check cookies in the Header
        final String authorization=request.getHeader("Authorization");
        if(authorization!=null && authorization.startsWith("Bearer ")){
            jwt=authorization.substring(7);
        }
        //if not found in the Header then check in the Cookies
        if(jwt==null){
            Cookie []cookies=request.getCookies();
            if(cookies!=null){
                for(Cookie cookie:cookies){
                    if(cookie.getName().equals("jwt")){
                        jwt=cookie.getValue();
                        break;
                    }
                }
            }
        }
        //after that validate the token
        if(jwt!=null){
            email=jwtUtil.extractEmail(jwt);
            if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = profileDetailService.loadUserByUsername(email);
                if(jwtUtil.validateToken(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}