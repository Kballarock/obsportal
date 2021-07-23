package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.contracts.OrganizationEmail;

import java.util.Set;

public interface OrganizationEmailRepository {

    OrganizationEmail save(OrganizationEmail organizationEmail, int reportGeneratorId);

    boolean delete(int id, int reportGeneratorId);

    OrganizationEmail get(int id, int reportGeneratorId);

    Set<OrganizationEmail> getAllByReportGeneratorId(int reportGeneratorId);
}