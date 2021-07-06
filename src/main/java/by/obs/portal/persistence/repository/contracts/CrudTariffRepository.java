package by.obs.portal.persistence.repository.contracts;

import by.obs.portal.persistence.model.contracts.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrudTariffRepository extends JpaRepository<Tariff, Integer> {

    @Query(value = "SELECT t.price FROM Tariff t WHERE :usersAmount >= t.minUsers AND :usersAmount <= t.maxUsers AND t.category = 2")
    double findReportGeneratorPrice(@Param("usersAmount") int usersAmount);
}