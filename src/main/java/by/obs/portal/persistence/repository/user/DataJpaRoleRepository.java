package by.obs.portal.persistence.repository.user;

import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DataJpaRoleRepository implements RoleRepository {

    private static final Sort SORT_NAME = Sort.by(Sort.Direction.ASC, "name");
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
    public boolean delete(int id) {
        return crudRoleRepository.delete(id) != 0;
    }

    @Override
    public Role create(Role role) {
        return crudRoleRepository.save(role);
    }

    @Override
    public List<Role> getAll() {
        return crudRoleRepository.findAll(SORT_NAME);
    }

    @Override
    public Role get(int id) {
        return crudRoleRepository.findById(id).orElse(null);
    }
}