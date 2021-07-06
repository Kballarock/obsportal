package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.user.PasswordResetToken;

public interface PasswordResetTokenRepository {

    PasswordResetToken getByToken(String token);

    void save(PasswordResetToken passwordResetToken);

    PasswordResetToken getByUserId(int id);

    void deleteAllByUserId(int id);
}