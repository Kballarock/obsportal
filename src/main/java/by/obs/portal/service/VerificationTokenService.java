package by.obs.portal.service;

import by.obs.portal.persistence.model.user.User;
import by.obs.portal.persistence.model.user.VerificationToken;
import by.obs.portal.persistence.repository.UserRepository;
import by.obs.portal.persistence.repository.VerificationTokenRepository;
import by.obs.portal.utils.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static by.obs.portal.common.Constants.*;
import static by.obs.portal.utils.ValidationUtil.checkNotFound;
import static by.obs.portal.utils.ValidationUtil.checkNotFoundWithId;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class VerificationTokenService {

    VerificationTokenRepository tokenRepository;
    UserRepository userRepository;

    public VerificationTokenService(VerificationTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public User getByVerificationToken(final String verificationToken) {
        final var token = tokenRepository.getByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        throw new NotFoundException("User with verification token: " + verificationToken + " not found.");
    }

    public VerificationToken getVerificationToken(final String verificationToken) {
        return checkNotFound(tokenRepository.getByToken(verificationToken),
                "Verification token " + verificationToken + " not found.");
    }

    public void createVerificationToken(final User user, final String token) {
        final var newToken = new VerificationToken(token, user);
        tokenRepository.createVerificationToken(newToken);
    }

    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        var vToken = tokenRepository.getByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID().toString());
        return tokenRepository.generateNewVerificationToken(vToken);
    }

    void deleteVerificationTokenById(int id) {
        checkNotFoundWithId(tokenRepository.delete(id), id);
    }

    public String validateVerificationToken(String token) {
        final var verificationToken = tokenRepository.getByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final var user = verificationToken.getUser();
        if (LocalDateTime.now().isAfter(verificationToken.getExpiryDate())) {
            tokenRepository.delete(verificationToken.getId());
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        userRepository.save(user);
        return TOKEN_VALID;
    }
}