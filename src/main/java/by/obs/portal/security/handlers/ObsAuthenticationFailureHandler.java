package by.obs.portal.security.handlers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component("authenticationFailureHandler")
public class ObsAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    MessageSource messages;
    LocaleResolver localeResolver;

    @Autowired
    public ObsAuthenticationFailureHandler(
            @Qualifier("messageSource") MessageSource messages,
            @Qualifier("localeResolver") LocaleResolver localeResolver) {
        this.messages = messages;
        this.localeResolver = localeResolver;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/login?error=true");

        super.onAuthenticationFailure(request, response, exception);

        final var locale = localeResolver.resolveLocale(request);

        var errorMessage = messages.getMessage("authentication.message.badCredentials", null, locale);

        if (exception.getMessage()
                .equalsIgnoreCase("User is disabled")) {
            errorMessage = messages.getMessage("authentication.message.disabled", null, locale);
        } else if (exception.getMessage()
                .equalsIgnoreCase("User account has expired")) {
            errorMessage = messages.getMessage("authentication.message.expired", null, locale);
        } else if (exception.getMessage().equalsIgnoreCase("blocked")) {
            errorMessage = messages.getMessage("authentication.message.blocked", null, locale);
        }
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}