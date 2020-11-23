package by.obs.portal.security.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("logoutSuccessHandler")
public class ObsLogoutSuccessHandler implements LogoutSuccessHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {

        final var session = request.getSession();
        if (session != null) {
            session.removeAttribute("user");
        }

        if (authentication != null) {
            log.debug("User: " + "[ " + authentication.getName() + " ]"
                    + " logout successfully and redirect to 'Login' page.");
        }
        response.sendRedirect("login?logout=true");
    }
}