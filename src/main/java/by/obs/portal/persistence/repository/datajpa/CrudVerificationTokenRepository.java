package by.obs.portal.persistence.repository.datajpa;

import by.obs.portal.persistence.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrudVerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {

    VerificationToken findByToken(String token);

    VerificationToken findByUserId(int id);

    @Modifying
    @Query("DELETE FROM VerificationToken v WHERE v.id=:id")
    int delete(@Param("id") int id);
}