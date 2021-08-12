package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.user.Role;

import java.util.List;

public interface RoleRepository {

    Role getByName(String name);

    boolean delete(int id);

    Role create(Role role);

    List<Role> getAll();

    Role get(int id);
}