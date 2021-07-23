package by.obs.portal.service;

import by.obs.portal.persistence.model.contracts.ReportGenerator;
import by.obs.portal.persistence.repository.ReportGeneratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static by.obs.portal.utils.ValidationUtil.checkNotFoundWithId;

@Service
@Transactional
public class ReportGeneratorService {

    private final ReportGeneratorRepository reportGeneratorRepository;

    @Autowired
    public ReportGeneratorService(ReportGeneratorRepository reportGeneratorRepository) {
        this.reportGeneratorRepository = reportGeneratorRepository;
    }

    public ReportGenerator create(final ReportGenerator reportGenerator) {
        Assert.notNull(reportGenerator, "ReportGenerator must not be null");
        return reportGeneratorRepository.save(reportGenerator);
    }

    public void delete(final int id) {
        checkNotFoundWithId(reportGeneratorRepository.delete(id), id);
    }

    public ReportGenerator get(final int id) {
        return checkNotFoundWithId(reportGeneratorRepository.get(id), id);
    }

    public List<ReportGenerator> getAll() {
        return reportGeneratorRepository.getAll();
    }

    public void update(final ReportGenerator reportGenerator) {
        Assert.notNull(reportGenerator, "User must not be null");
        reportGeneratorRepository.save(reportGenerator);
    }
}