package by.obs.portal.persistence.repository.datajpa;

import by.obs.portal.persistence.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrudTariffRepository extends JpaRepository<Tariff, Integer> {

    @Query(value = "SELECT t.price FROM Tariff t WHERE :usersAmount >= t.minUsers AND :usersAmount <= t.maxUsers AND t.category = 2")
    double findReportGeneratorPrice(@Param("usersAmount") int usersAmount);
}