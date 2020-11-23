package by.obs.portal.persistence.repository.datajpa;

import by.obs.portal.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrudUserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id=:id")
    int delete(@Param("id") int id);
}