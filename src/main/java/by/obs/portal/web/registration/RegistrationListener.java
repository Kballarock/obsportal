package by.obs.portal.web.registration;

import by.obs.portal.persistence.model.User;
import by.obs.portal.service.VerificationTokenService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent> {

    VerificationTokenService verificationTokenService;
    MessageSource messages;
    JavaMailSender mailSender;
    Environment environment;

    @Autowired
    public RegistrationListener(VerificationTokenService verificationTokenService,
                                @Qualifier("messageSource") MessageSource messages,
                                JavaMailSender mailSender, Environment environment) {
        this.verificationTokenService = verificationTokenService;
        this.messages = messages;
        this.mailSender = mailSender;
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(final @NonNull RegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final RegistrationCompleteEvent registrationCompleteEvent) {
        final var user = registrationCompleteEvent.getUser();
        final var token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);

        final var email = emailMessage(registrationCompleteEvent, user, token);
        mailSender.send(email);
    }

    private SimpleMailMessage emailMessage(final RegistrationCompleteEvent event, final User user, final String token) {
        final var recipientAddress = user.getEmail();
        final var subject = messages.getMessage("message.confirm.registration", null, event.getLocale());
        final var confirmationUrl = event.getAppUrl() + "/confirmRegistration.html?token=" + token;
        final var message = messages.getMessage("message.registrationConfirm", null, event.getLocale());
        final var email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n\n" + confirmationUrl);
        email.setFrom(Objects.requireNonNull(environment.getProperty("spring.mail.username")));
        return email;
    }
}