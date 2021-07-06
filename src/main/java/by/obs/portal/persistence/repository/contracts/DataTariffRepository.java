package by.obs.portal.persistence.repository.contracts;

import by.obs.portal.persistence.model.contracts.Tariff;
import by.obs.portal.persistence.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DataTariffRepository implements TariffRepository {

    private final CrudTariffRepository tariffRepository;

    @Autowired
    public DataTariffRepository(CrudTariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    @Override
    public double getReportGeneratorPrice(int usersAmount) {
        return tariffRepository.findReportGeneratorPrice(usersAmount);
    }

    @Override
    public List<Tariff> getAll() {
        return tariffRepository.findAll();
    }

    @Override
    public Tariff update(Tariff tariff) {
        return tariffRepository.save(tariff);
    }

    @Override
    public Tariff get(int id) {
        return tariffRepository.findById(id).orElse(null);
    }
}