package by.obs.portal.security.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("accessDeniedHandler")
public class ObsAccessDeniedHandler implements AccessDeniedHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exc) throws IOException {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.warn("User: " + "[" + auth.getName() + "]" + " attempted to access the protected URL: " +
                    request.getRequestURI());
        }

        response.sendRedirect(request.getContextPath() + "/403");
    }
}