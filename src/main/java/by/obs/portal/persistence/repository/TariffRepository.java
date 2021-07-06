package by.obs.portal.persistence.repository;

import by.obs.portal.persistence.model.Tariff;

import java.util.List;

public interface TariffRepository {

    double getReportGeneratorPrice(int usersAmount);

    List<Tariff> getAll();

    Tariff update(Tariff tariff);

    Tariff get(int id);
}