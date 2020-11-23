package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.Privilege;

public interface PrivilegeRepository {

    Privilege getByName(String name);

    void delete(Privilege privilege);

    Privilege create(Privilege privilege);
}