package by.obs.portal.web.registration;

import by.obs.portal.persistence.model.user.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegistrationCompleteEvent extends ApplicationEvent {

    String appUrl;
    Locale locale;
    User user;

    public RegistrationCompleteEvent(final User user, final Locale locale, final String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}