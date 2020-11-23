package by.obs.portal.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static by.obs.portal.common.Constants.TOKEN_PARAM;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("obsUserDetailsService")
    UserDetailsService userDetailsService;

    @Autowired
    JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain) throws IOException, ServletException {

        var token = req.getParameter(TOKEN_PARAM);
        String username = null;
        if (token != null) {
            try {
                username = jwtProvider.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                log.error("An error occurred during getting username from token", e);
            } catch (ExpiredJwtException e) {
                log.warn("The token is expired and not valid anymore", e);
            } catch (SignatureException e) {
                log.error("Authentication Failed. Username or Password not valid.");
            }
        }

        if (username != null) {
            var userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtProvider.validateToken(token, userDetails)) {
                var authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                log.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(req, res);
    }
}