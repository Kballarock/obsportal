package by.obs.portal.persistence.repository.datajpa;

import by.obs.portal.persistence.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrudPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    PasswordResetToken findByToken(String token);
}