package by.obs.portal.security.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static by.obs.portal.common.Constants.*;
import static by.obs.portal.utils.SecurityUtil.getUserAuthority;

@Component("authenticationSuccessHandler")
public class ObsAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

    protected void handle(final HttpServletRequest request, final HttpServletResponse response,
                          final Authentication authentication) throws IOException {
        final var targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(final Authentication authentication) {
        var userAuthorities = getUserAuthority(authentication.getAuthorities());

        if (userAuthorities.equals(USER)) {
            return HOME_URL;
        } else if (userAuthorities.equals(ADMIN)) {
            return ADMIN_URL;
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
}