package by.obs.portal.persistence.repository.user;

import by.obs.portal.persistence.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface CrudRoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String name);

    @Override
    void delete(@NonNull Role role);
}