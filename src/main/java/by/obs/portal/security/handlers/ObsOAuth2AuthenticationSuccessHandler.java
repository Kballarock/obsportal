package by.obs.portal.security.handlers;

import by.obs.portal.security.ObsUserDetailsService;
import by.obs.portal.security.jwt.JwtProvider;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static by.obs.portal.common.Constants.*;
import static by.obs.portal.utils.SecurityUtil.getUserAuthority;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ObsOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger log = LoggerFactory.getLogger(getClass());
    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    final JwtProvider jwtProvider;
    final ObsUserDetailsService userDetailsService;

    @Autowired
    public ObsOAuth2AuthenticationSuccessHandler(JwtProvider jwtProvider, ObsUserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }


    protected void handle(final HttpServletRequest request, final HttpServletResponse response,
                          final Authentication authentication) throws IOException {
        final var targetOAuth2Url = determineTargetOAuth2Url(authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetOAuth2Url);
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetOAuth2Url);
    }

    private String determineTargetOAuth2Url(final Authentication authentication) {

        var oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        var email = (String) attributes.get("email");
        var user = userDetailsService.loadUserByUsername(email);
        var token = jwtProvider.generateToken(user);

        var userAuthorities = getUserAuthority(user.getAuthorities());

        if (userAuthorities.equals(USER)) {
            return getRedirectionUrl(HOME_URL, token);
        } else if (userAuthorities.equals(ADMIN)) {
            return getRedirectionUrl(ADMIN_URL, token);
        } else {
            throw new IllegalStateException();
        }
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    private String getRedirectionUrl(String url, String token) {
        return UriComponentsBuilder.fromUriString(url)
                .queryParam(TOKEN_PARAM, token)
                .build().toUriString();
    }
}