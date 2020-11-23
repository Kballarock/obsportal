package by.obs.portal.security;

import by.obs.portal.persistence.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ObsRememberMeServices extends PersistentTokenBasedRememberMeServices {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    UserRepository userRepository;

    GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    PersistentTokenRepository tokenRepository;
    String key;

    public ObsRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
        this.tokenRepository = tokenRepository;
        this.key = key;
    }

    @Override
    protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) {
        var authUser = userRepository.getByEmail(user.getUsername());
        var auth = new RememberMeAuthenticationToken(key, authUser, authoritiesMapper.mapAuthorities(user.getAuthorities()));
        auth.setDetails(authenticationDetailsSource.buildDetails(request));
        return auth;
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Authentication successfulAuthentication) {

        String username = successfulAuthentication.getName();
        log.debug("Creating new persistent login for user [ " + username + " ]");
        var persistentToken = new PersistentRememberMeToken(username, generateSeriesData(), generateTokenData(), new Date());
        try {
            tokenRepository.createNewToken(persistentToken);
            addCookie(persistentToken, request, response);
        } catch (Exception e) {
            log.error("Failed to save persistent token ", e);
        }
    }

    private void addCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(new String[]{token.getSeries(), token.getTokenValue()}, getTokenValiditySeconds(), request, response);
    }
}