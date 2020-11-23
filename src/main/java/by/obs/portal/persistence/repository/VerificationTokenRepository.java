package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.VerificationToken;

public interface VerificationTokenRepository {

    VerificationToken save(VerificationToken verificationToken);

    VerificationToken getByToken(String token);

    boolean delete(int id);

    void createVerificationToken(VerificationToken verificationToken);

    VerificationToken generateNewVerificationToken(VerificationToken verificationToken);

    VerificationToken getByUserId(int id);
}