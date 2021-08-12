package by.obs.portal.persistence.repository.user;

import by.obs.portal.persistence.model.user.Privilege;
import by.obs.portal.persistence.repository.PrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DataJpaPrivilegeRepository implements PrivilegeRepository {

    private final CrudPrivilegeRepository crudPrivilegeRepository;

    @Autowired
    public DataJpaPrivilegeRepository(CrudPrivilegeRepository crudPrivilegeRepository) {
        this.crudPrivilegeRepository = crudPrivilegeRepository;
    }

    @Override
    public void delete(Privilege privilege) {
        crudPrivilegeRepository.delete(privilege);
    }

    @Override
    public Privilege create(Privilege privilege) {
        return crudPrivilegeRepository.save(privilege);
    }

    @Override
    public Privilege get(int id) {
        return crudPrivilegeRepository.findById(id).orElse(null);
    }
}