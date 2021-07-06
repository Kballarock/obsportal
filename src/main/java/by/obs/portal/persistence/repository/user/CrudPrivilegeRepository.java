package by.obs.portal.persistence.repository.user;

import by.obs.portal.persistence.model.user.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrudPrivilegeRepository extends JpaRepository<Privilege, Integer> {

    Privilege findByName(String name);

    @Override
    void delete(Privilege privilege);
}