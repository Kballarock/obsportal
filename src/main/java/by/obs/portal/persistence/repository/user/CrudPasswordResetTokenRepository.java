package by.obs.portal.persistence.repository.user;

import by.obs.portal.persistence.model.user.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrudPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUserId(int id);

    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.user.id=:id")
    void deleteAllByUserId(@Param("id") int id);
}