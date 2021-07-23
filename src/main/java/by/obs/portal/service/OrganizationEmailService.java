package by.obs.portal.service;

import by.obs.portal.persistence.model.contracts.OrganizationEmail;
import by.obs.portal.persistence.repository.OrganizationEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Set;

import static by.obs.portal.utils.ValidationUtil.checkNotFoundWithId;

@Service
@Transactional
public class OrganizationEmailService {

    private final OrganizationEmailRepository emailRepository;

    @Autowired
    public OrganizationEmailService(OrganizationEmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public OrganizationEmail get(int id, int reportGeneratorId) {
        return checkNotFoundWithId(emailRepository.get(id, reportGeneratorId), id);
    }

    public void delete(int id, int reportGeneratorId) {
        checkNotFoundWithId(emailRepository.delete(id, reportGeneratorId), id);
    }

    public Set<OrganizationEmail> getAllByReportGeneratorId(int reportGeneratorId) {
        return emailRepository.getAllByReportGeneratorId(reportGeneratorId);
    }

    public void update(OrganizationEmail organizationEmail, int reportGeneratorId) {
        Assert.notNull(organizationEmail, "Organization email must not be null");
        checkNotFoundWithId(emailRepository.save(organizationEmail, reportGeneratorId), organizationEmail.id());
    }

    public OrganizationEmail create(OrganizationEmail organizationEmail, int reportGeneratorId) {
        Assert.notNull(organizationEmail, "Organization email must not be null");
        return emailRepository.save(organizationEmail, reportGeneratorId);
    }
}