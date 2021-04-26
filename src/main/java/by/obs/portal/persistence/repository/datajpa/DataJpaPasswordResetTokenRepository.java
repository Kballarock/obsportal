package by.obs.portal.persistence.repository.datajpa;

import by.obs.portal.persistence.model.PasswordResetToken;
import by.obs.portal.persistence.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DataJpaPasswordResetTokenRepository implements PasswordResetTokenRepository {

    private final CrudPasswordResetTokenRepository repository;

    @Autowired
    public DataJpaPasswordResetTokenRepository(CrudPasswordResetTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public PasswordResetToken getByToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public void save(PasswordResetToken passwordResetToken) {
        repository.save(passwordResetToken);
    }

    @Override
    public PasswordResetToken getByUserId(int id) {
        return repository.findByUserId(id);
    }

    @Override
    public void deleteAllByUserId(int id) {
        repository.deleteAllByUserId(id);
    }
}