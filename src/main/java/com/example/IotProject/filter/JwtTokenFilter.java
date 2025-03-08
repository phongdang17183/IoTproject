package com.example.IotProject.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.IotProject.component.CustomAccessDeniedHandler;
import com.example.IotProject.component.CustomAuthenticationEntryPoint;
import com.example.IotProject.component.JwtTokenUtil;
import com.example.IotProject.model.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.util.Pair;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Allow all requests without authentication
        // filterChain.doFilter(request, response);
        try {
            if (isBypassToken(request)) {
                filterChain.doFilter(request, response); // enable bypass
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationException("Unauthorized") {
                };
            }
            final String token = authHeader.substring(7);
            final String username = jwtTokenUtil.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = (User) userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(token, user)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            customAuthenticationEntryPoint.commence(request, response, new AuthenticationException("Unauthorized") {
            });
        }

    }

    private boolean isBypassToken(@NotNull HttpServletRequest request) {
        // These are the endpoints that don't need authentication(token)
        final List<Pair<String, String>> bypassTokens = List.of(
                Pair.of(apiPrefix + "/login", "POST"),
                Pair.of(apiPrefix + "/register", "POST"),
                Pair.of(apiPrefix + "/send", "POST"),
                Pair.of(apiPrefix + "/test", "GET"),
                Pair.of("/ws", "GET"),
                Pair.of("/images/", "GET"));
        for (Pair<String, String> bypassToken : bypassTokens) {
            /*
             * should use getServletPath() instead of getRequestURI()
             * to get the path of the request
             * because:
             * getRequestURI() returns the full URI of the request
             * (including protocol, host, port, context path, servlet path và query string)
             * getServletPath() returns the path of the request (Ex: /api/v1/users/login)
             */
            if (request.getServletPath().contains(bypassToken.getFirst()) &&
                    request.getMethod().equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
}
