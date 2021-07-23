package by.obs.portal.persistence.repository.contracts;

import by.obs.portal.persistence.model.contracts.ReportGenerator;
import by.obs.portal.persistence.repository.ReportGeneratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DataReportGeneratorRepository implements ReportGeneratorRepository {

    private static final Sort SORT_CONTRACT_NUMBER = Sort.by(Sort.Direction.ASC, "contractNumber");
    private final CrudReportGeneratorRepository reportGeneratorRepository;

    @Autowired
    public DataReportGeneratorRepository(CrudReportGeneratorRepository reportGeneratorRepository) {
        this.reportGeneratorRepository = reportGeneratorRepository;
    }

    @Override
    public ReportGenerator save(ReportGenerator reportGenerator) {
        return reportGeneratorRepository.save(reportGenerator);
    }

    @Override
    public boolean delete(int id) {
        return reportGeneratorRepository.delete(id) != 0;
    }

    @Override
    public ReportGenerator get(int id) {
        return reportGeneratorRepository.findById(id).orElse(null);
    }

    @Override
    public List<ReportGenerator> getAll() {
        return reportGeneratorRepository.findAll(SORT_CONTRACT_NUMBER);
    }
}