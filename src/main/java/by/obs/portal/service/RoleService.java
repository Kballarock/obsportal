package by.obs.portal.service;

import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.persistence.repository.PrivilegeRepository;
import by.obs.portal.persistence.repository.RoleRepository;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static by.obs.portal.utils.ValidationUtil.checkNotFoundWithId;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }

    public Role create(final Role role) {
        Assert.notNull(role, "Role must not be null");
        return roleRepository.create(role);
    }

    public void delete(final int id) {
        checkNotFoundWithId(roleRepository.delete(id), id);
    }

    public Role get(final int id) {
        return checkNotFoundWithId(roleRepository.get(id), id);
    }

    public List<Role> getAll() {
        return roleRepository.getAll();
    }

    public void update(final Role role) {
        Assert.notNull(role, "Role must not be null");
        roleRepository.create(role);
    }

    public void addPrivilege(final int id, final int privilegeId) {
        var role = roleRepository.get(id);
        var privileges = role.getPrivileges();
        var newPrivilege = privilegeRepository.get(privilegeId);
        privileges.add(newPrivilege);
        role.setPrivileges(privileges);
        roleRepository.create(role);
    }

    public void deletePrivilege(final int id, final int privilegeId) {
        var role = roleRepository.get(id);
        var privileges = role.getPrivileges();
        privileges.remove(privilegeRepository.get(privilegeId));
        role.setPrivileges(privileges);
        roleRepository.create(role);
    }
}