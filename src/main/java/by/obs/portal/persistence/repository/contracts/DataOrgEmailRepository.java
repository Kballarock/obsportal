package by.obs.portal.persistence.repository.contracts;

import by.obs.portal.persistence.model.contracts.OrganizationEmail;
import by.obs.portal.persistence.repository.OrganizationEmailRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataOrgEmailRepository implements OrganizationEmailRepository {

    CrudOrgEmailRepository orgEmailRepository;
    CrudReportGeneratorRepository reportGeneratorRepository;

    @Autowired
    public DataOrgEmailRepository(CrudOrgEmailRepository orgEmailRepository,
                                  CrudReportGeneratorRepository reportGeneratorRepository) {
        this.orgEmailRepository = orgEmailRepository;
        this.reportGeneratorRepository = reportGeneratorRepository;
    }

    @Override
    public OrganizationEmail save(OrganizationEmail organizationEmail, int reportGeneratorId) {
        if (!organizationEmail.isNew() && get(organizationEmail.getId(), reportGeneratorId) == null) {
            return null;
        }
        organizationEmail.setReportGenerator(reportGeneratorRepository.getOne(reportGeneratorId));
        return orgEmailRepository.save(organizationEmail);
    }

    @Override
    public boolean delete(int id, int reportGeneratorId) {
        return orgEmailRepository.delete(id, reportGeneratorId) != 0;
    }

    @Override
    public OrganizationEmail get(int id, int reportGeneratorId) {
        return orgEmailRepository.findById(id)
                .filter(email -> email.getReportGenerator().getId() == reportGeneratorId).orElse(null);
    }

    @Override
    public Set<OrganizationEmail> getAllByReportGeneratorId(int reportGeneratorId) {
        return orgEmailRepository.getAllByReportGeneratorById(reportGeneratorId);
    }
}