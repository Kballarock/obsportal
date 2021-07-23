package by.obs.portal.web.controller.contract;

import by.obs.portal.persistence.model.contracts.ReportGenerator;
import by.obs.portal.service.ReportGeneratorService;
import by.obs.portal.validation.view.ErrorSequence;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax/report/generator")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReportGeneratorUIController {

    ReportGeneratorService reportGeneratorService;

    @Autowired
    public ReportGeneratorUIController(ReportGeneratorService reportGeneratorService) {
        this.reportGeneratorService = reportGeneratorService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReportGenerator> getAll() {
        return reportGeneratorService.getAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReportGenerator get(@PathVariable int id) {
        return reportGeneratorService.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        reportGeneratorService.delete(id);
    }

    @PostMapping
    public void createOrUpdate(@Validated(ErrorSequence.class) ReportGenerator reportGenerator) {
        if (reportGenerator.isNew()) {

            System.out.println("REP:" + reportGenerator.toString());


            reportGeneratorService.create(reportGenerator);
        } else {
            reportGeneratorService.update(reportGenerator);
        }
    }
}