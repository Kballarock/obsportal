package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.PasswordResetToken;

public interface PasswordResetTokenRepository {

    PasswordResetToken getByToken(String token);

    void save(PasswordResetToken passwordResetToken);
}