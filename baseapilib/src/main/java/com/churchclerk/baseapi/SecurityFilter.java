/*

 */
package com.churchclerk.baseapi;

import com.churchclerk.securityapi.SecurityApi;
import com.churchclerk.securityapi.SecurityToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 *
 */
@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String	secret;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        var auth = request.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {

            var token	= new SecurityToken();

            token.setJwt(auth.substring(7));
            token.setSecret(secret);

            if (SecurityApi.process(token) == true) {
                SecurityContextHolder.getContext().setAuthentication(
                        createAuthenticationToken(
                                createUserDetails(token),
                                new WebAuthenticationDetailsSource().buildDetails(request),
                                token
                        )
                );
            }
            else {
                log.warn("JWT is expired or invalid!");
            }
        } else {
            log.warn("Authorization header missing or is not Bearer!");
        }

        chain.doFilter(request, response);
    }

    private UserDetails createUserDetails(SecurityToken token) {
        return User.withUsername(token.getId())
                .password(token.getLocation())
                .roles(token.getRoles().split(","))
                .build();
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(UserDetails ud, Object details, SecurityToken st) {
        GrantedAuthority    ga;

        var token = new UsernamePasswordAuthenticationToken(
                            ud, st, ud.getAuthorities()
        );

        token.setDetails(details);
        return token;
    }
}
