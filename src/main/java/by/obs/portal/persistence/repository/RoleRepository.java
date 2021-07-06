package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.user.Role;

public interface RoleRepository {

    Role getByName(String name);

    void delete(Role role);

    Role create(Role role);
}