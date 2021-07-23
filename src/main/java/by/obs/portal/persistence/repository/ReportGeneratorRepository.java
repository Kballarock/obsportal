package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.contracts.ReportGenerator;

import java.util.List;

public interface ReportGeneratorRepository {

    ReportGenerator save(ReportGenerator reportGenerator);

    boolean delete(int id);

    ReportGenerator get(int id);

    List<ReportGenerator> getAll();
}