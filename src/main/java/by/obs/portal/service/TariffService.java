package by.obs.portal.service;

import by.obs.portal.persistence.model.contracts.Tariff;
import by.obs.portal.persistence.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static by.obs.portal.utils.ValidationUtil.checkNotFoundWithId;

@Service
public class TariffService {

    private final TariffRepository tariffRepository;

    @Autowired
    public TariffService(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    public List<Tariff> getAll() {
        return tariffRepository.getAll();
    }

    public void update(Tariff tariff) {
        tariffRepository.update(tariff);
    }

    public Tariff get(int id) {
        return checkNotFoundWithId(tariffRepository.get(id), id);
    }

    public double getReportGeneratorPrice(int usersAmount) {
        return tariffRepository.getReportGeneratorPrice(usersAmount);
    }
}