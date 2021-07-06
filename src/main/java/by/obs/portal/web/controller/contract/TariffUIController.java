package by.obs.portal.web.controller.contract;

import by.obs.portal.persistence.model.contracts.Tariff;
import by.obs.portal.service.TariffService;
import by.obs.portal.validation.view.ErrorSequence;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax/admin/tariff")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TariffUIController {

    TariffService tariffService;

    @Autowired
    public TariffUIController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Tariff> getAll() {
        return tariffService.getAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Tariff get(@PathVariable int id) {
        return tariffService.get(id);
    }

    @PostMapping
    public void update(@Validated(ErrorSequence.class) Tariff tariff) {
        tariffService.update(tariff);
    }
}