package by.obs.portal.web.controller.contract;

import by.obs.portal.persistence.model.contracts.Tariff;
import by.obs.portal.service.TariffService;
import by.obs.portal.validation.view.ErrorSequence;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static by.obs.portal.utils.ValidationUtil.assureIdConsistent;

@RestController
@RequestMapping(value = TariffRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TariffRestController {

    static final String REST_URL = "/rest/admin/tariff";
    TariffService tariffService;

    @Autowired
    public TariffRestController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping
    public List<Tariff> getAll() {
        return tariffService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Tariff get(@PathVariable int id) {
        return tariffService.get(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(ErrorSequence.class) @RequestBody Tariff tariff, @PathVariable int id) {
        assureIdConsistent(tariff, id);
        tariffService.update(tariff);
    }
}