package by.obs.portal.persistence.repository.contracts;

import by.obs.portal.persistence.model.contracts.ReportGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrudReportGeneratorRepository extends JpaRepository<ReportGenerator, Integer> {

    @Modifying
    @Query("DELETE FROM ReportGenerator r WHERE r.id=:id")
    int delete(@Param("id") int id);
}