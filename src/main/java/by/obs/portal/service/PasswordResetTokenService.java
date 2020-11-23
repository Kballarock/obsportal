package by.obs.portal.service;

import by.obs.portal.persistence.model.User;
import by.obs.portal.persistence.repository.PasswordResetTokenRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

import static by.obs.portal.common.Constants.*;
import static by.obs.portal.utils.ValidationUtil.checkNotFound;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PasswordResetTokenService {

    PasswordResetTokenRepository passwordTokenRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordTokenRepository) {
        this.passwordTokenRepository = passwordTokenRepository;
    }

    public void createPasswordResetToken(final User user, final String token) {
        final var myToken = new by.obs.portal.persistence.model.PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public by.obs.portal.persistence.model.PasswordResetToken getPasswordResetToken(final String token) {
        return checkNotFound(passwordTokenRepository.getByToken(token), "Password reset token: " + token + " not found.");
    }

    public String validatePasswordResetToken(int id, String token) {

        final var passToken = passwordTokenRepository.getByToken(token);
        if ((passToken == null) || (passToken.getUser().getId() != id)) {
            return TOKEN_INVALID;
        }

        if (LocalDateTime.now().isAfter(passToken.getExpiryDate())) {
            return TOKEN_EXPIRED;
        }

        final var user = passToken.getUser();
        System.out.println();
        final var auth = new UsernamePasswordAuthenticationToken(user, null,
                Collections.singletonList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));

        SecurityContextHolder.getContext().setAuthentication(auth);
        return TOKEN_VALID;
    }
}