package by.obs.portal.persistence.repository.contracts;

import by.obs.portal.persistence.model.contracts.OrganizationEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface CrudOrgEmailRepository extends JpaRepository<OrganizationEmail, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM OrganizationEmail o WHERE o.id = :id AND o.reportGenerator.id = :reportGeneratorId")
    int delete(@Param("id") int id, @Param("reportGeneratorId") int reportGeneratorId);

    @Query("SELECT o FROM OrganizationEmail o WHERE o.reportGenerator.id = :reportGeneratorId ORDER BY o.email ASC")
    Set<OrganizationEmail> getAllByReportGeneratorById(@Param("reportGeneratorId") int reportGeneratorId);
}