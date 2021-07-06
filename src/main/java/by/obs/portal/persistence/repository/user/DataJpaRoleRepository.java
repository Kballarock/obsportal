package by.obs.portal.persistence.repository.user;

import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DataJpaRoleRepository implements RoleRepository {

    private final CrudRoleRepository crudRoleRepository;

    @Autowired
    public DataJpaRoleRepository(CrudRoleRepository crudRoleRepository) {
        this.crudRoleRepository = crudRoleRepository;
    }

    @Override
    public Role getByName(String name) {
        return crudRoleRepository.findByName(name);
    }

    @Override
    public void delete(Role role) {
        crudRoleRepository.delete(role);
    }

    @Override
    public Role create(Role role) {
        return crudRoleRepository.save(role);
    }
}
