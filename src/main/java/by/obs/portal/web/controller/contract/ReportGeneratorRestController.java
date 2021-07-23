package by.obs.portal.web.controller.contract;

import by.obs.portal.persistence.model.contracts.ReportGenerator;
import by.obs.portal.service.ReportGeneratorService;
import by.obs.portal.utils.UriUtil;
import by.obs.portal.validation.view.ErrorSequence;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static by.obs.portal.utils.ValidationUtil.assureIdConsistent;

@RestController
@RequestMapping(value = ReportGeneratorRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReportGeneratorRestController {

    static final String REST_URL = "/rest/report/generator";
    ReportGeneratorService reportGeneratorService;

    @Autowired
    public ReportGeneratorRestController(ReportGeneratorService reportGeneratorService) {
        this.reportGeneratorService = reportGeneratorService;
    }

    @GetMapping
    public List<ReportGenerator> getAll() {
        return reportGeneratorService.getAll();
    }

    @GetMapping("/{id}")
    public ReportGenerator get(@PathVariable int id) {
        return reportGeneratorService.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportGenerator> createWithLocation(@Validated(ErrorSequence.class)
                                                              @RequestBody ReportGenerator reportGenerator) {
        ReportGenerator created = reportGeneratorService.create(reportGenerator);

        URI uriOfNewResource = UriUtil.createNewURI(REST_URL, created.getId());

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        reportGeneratorService.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(ErrorSequence.class) @RequestBody ReportGenerator reportGenerator, @PathVariable int id) {
        assureIdConsistent(reportGenerator, id);
        reportGeneratorService.update(reportGenerator);
    }
}