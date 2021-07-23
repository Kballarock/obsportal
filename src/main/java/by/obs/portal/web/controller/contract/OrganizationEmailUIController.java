package by.obs.portal.web.controller.contract;

import by.obs.portal.persistence.model.contracts.OrganizationEmail;
import by.obs.portal.service.OrganizationEmailService;
import by.obs.portal.validation.view.ErrorSequence;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static by.obs.portal.utils.ValidationUtil.assureIdConsistent;
import static by.obs.portal.utils.ValidationUtil.checkNew;

@RestController
@RequestMapping("/ajax/report/generator/{reportGeneratorId}/emailList")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrganizationEmailUIController {

    OrganizationEmailService organizationEmailService;

    @Autowired
    public OrganizationEmailUIController(OrganizationEmailService organizationEmailService) {
        this.organizationEmailService = organizationEmailService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<OrganizationEmail> getAllByReportGeneratorId(@PathVariable("reportGeneratorId") int reportGeneratorId) {
        return organizationEmailService.getAllByReportGeneratorId(reportGeneratorId);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrganizationEmail get(@PathVariable("id") int id, @PathVariable("reportGeneratorId") int reportGeneratorId) {
        return organizationEmailService.get(id, reportGeneratorId);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id, @PathVariable("reportGeneratorId") int reportGeneratorId) {
        organizationEmailService.delete(id, reportGeneratorId);
    }

    @PostMapping
    public void createOrUpdate(@Validated(ErrorSequence.class) OrganizationEmail organizationEmail,
                               @PathVariable("reportGeneratorId") int reportGeneratorId) {
        if (organizationEmail.isNew()) {
            checkNew(organizationEmail);
            organizationEmailService.create(organizationEmail, reportGeneratorId);
        } else {
            assureIdConsistent(organizationEmail, organizationEmail.id());
            organizationEmailService.update(organizationEmail,  reportGeneratorId);
        }
    }
}