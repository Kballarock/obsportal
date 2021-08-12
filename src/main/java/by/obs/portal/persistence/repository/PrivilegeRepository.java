package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.user.Privilege;

public interface PrivilegeRepository {

    void delete(Privilege privilege);

    Privilege create(Privilege privilege);

    Privilege get(int id);
}