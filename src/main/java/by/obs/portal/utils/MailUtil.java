package by.obs.portal.utils;

import by.obs.portal.persistence.model.User;
import by.obs.portal.persistence.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

@Component
public class MailUtil {

    private static MessageSource messages;
    private static Environment env;

    @Autowired
    public MailUtil(@Qualifier("messageSource") MessageSource messages, Environment env) {
        MailUtil.messages = messages;
        MailUtil.env = env;
    }

    public static SimpleMailMessage resendVerificationTokenEmail(final String contextPath, final Locale locale,
                                                                 final VerificationToken newToken, final User user) {
        String confirmationUrl;
        if (contextPath.contains("rest")) {
            confirmationUrl = contextPath + "/confirmRegistration?token=" + newToken.getToken();
        } else {
            confirmationUrl = contextPath + "/confirmRegistration.html?token=" + newToken.getToken();
        }

        final var subject = messages.getMessage("message.confirm.registration", null, locale);
        final var message = messages.getMessage("message.registrationConfirm", null, locale);
        final var email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(message + " \r\n\n" + confirmationUrl);
        email.setTo(user.getEmail());
        email.setFrom(Objects.requireNonNull(env.getProperty("spring.mail.username")));
        return email;
    }

    public static SimpleMailMessage resendResetPasswordTokenEmail(final String contextPath, final Locale locale,
                                                                  final String token, final User user) {
        final var userName = new String[]{user.getName()};
        String resetPasswordUrl;

        if (contextPath.contains("rest")) {
            resetPasswordUrl = contextPath + "/service/changeNewPassword?id=" + user.getId() + "&token=" + token;
        } else {
            resetPasswordUrl = contextPath + "/service/changeNewPassword.html?id=" + user.getId() + "&token=" + token;
        }

        final var message = messages.getMessage("message.resetPassword.text", userName, locale);
        final var messageInfo = messages.getMessage("message.resetPassword.text2", userName, locale);
        final var subject = messages.getMessage("message.resetPassword", null, locale);
        final var email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message + " \r\n\n" + resetPasswordUrl + " \r\n\n" + messageInfo);
        email.setFrom(Objects.requireNonNull(env.getProperty("spring.mail.username")));
        return email;
    }
}