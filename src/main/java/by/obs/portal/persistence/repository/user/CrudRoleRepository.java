package by.obs.portal.persistence.repository.user;

import by.obs.portal.persistence.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrudRoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String name);

    @Modifying
    @Query("DELETE FROM Role r WHERE r.id=:id")
    int delete(@Param("id") int id);
}