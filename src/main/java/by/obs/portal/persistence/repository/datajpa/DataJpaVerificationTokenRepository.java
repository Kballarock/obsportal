package by.obs.portal.persistence.repository.datajpa;

import by.obs.portal.persistence.model.VerificationToken;
import by.obs.portal.persistence.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DataJpaVerificationTokenRepository implements VerificationTokenRepository {

    private final CrudVerificationTokenRepository repository;

    @Autowired
    public DataJpaVerificationTokenRepository(CrudVerificationTokenRepository verificationTokenRepository) {
        this.repository = verificationTokenRepository;
    }

    @Override
    public VerificationToken generateNewVerificationToken(VerificationToken verificationToken) {
        return repository.save(verificationToken);
    }

    @Override
    public VerificationToken getByUserId(int id) {
        return repository.findByUserId(id);
    }

    @Override
    public VerificationToken save(VerificationToken verificationToken) {
        return repository.saveAndFlush(verificationToken);
    }

    @Override
    public VerificationToken getByToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public boolean delete(int id) {
        return repository.delete(id) != 0;
    }

    @Override
    public void createVerificationToken(VerificationToken verificationToken) {
        repository.save(verificationToken);
    }
}