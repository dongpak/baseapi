/*

 */
package com.churchclerk.baseapi;

import com.churchclerk.securityapi.SecurityApi;
import com.churchclerk.securityapi.SecurityToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SecurityFilter extends OncePerRequestFilter {

    private static Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Value("${jwt.secret}")
    private String	secret;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {

            SecurityToken token	= new SecurityToken();

            token.setJwt(auth.substring(7));
            token.setSecret(secret);

            if (SecurityApi.process(token) == true
            &&  token.expired() == false) {
                SecurityContextHolder.getContext().setAuthentication(
                        createAuthenticationToken(
                                createUserDetails(token),
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        )
                );
            }
            else {
                logger.warn("JWT is expired or invalid!");
            }
        } else {
            logger.warn("Authorization header missing or is not Bearer!");
        }

        chain.doFilter(request, response);
    }

    private UserDetails createUserDetails(SecurityToken token) {
        return User.withUsername(token.getId()).password("test").roles("super").build();
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(UserDetails ud, Object details) {
        GrantedAuthority    ga;

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            ud, null, ud.getAuthorities()
        );

        token.setDetails(details);
        return token;
    }
}
