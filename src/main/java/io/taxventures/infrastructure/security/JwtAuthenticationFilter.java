package io.taxventures.infrastructure.security;

import io.taxventures.service.token.TokenService;
import io.taxventures.service.user.UserPrincipal;
import io.taxventures.service.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    public JwtAuthenticationFilter(@Autowired TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeaderIsInvalid(authorizationHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken token = createToken(authorizationHeader);
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }

    private boolean authorizationHeaderIsInvalid(String authorizationHeader) {
        return (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "));
    }

    private UsernamePasswordAuthenticationToken createToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        UserPrincipal userPrincipal = tokenService.parseToken(token);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (userPrincipal.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.name()));
        }
        return new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
    }
}
