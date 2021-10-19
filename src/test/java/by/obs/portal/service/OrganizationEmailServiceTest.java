package by.obs.portal.service;

import by.obs.portal.persistence.model.contracts.OrganizationEmail;
import by.obs.portal.utils.exception.MessageUtil;
import by.obs.portal.utils.exception.NotFoundException;
import by.obs.portal.validation.view.ErrorSequence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import javax.validation.*;

import java.util.Set;

import static by.obs.portal.testdata.OrganizationEmailTestData.*;
import static by.obs.portal.testdata.ReportGeneratorTestData.REPORT_GENERATOR_1;
import static by.obs.portal.testdata.ReportGeneratorTestData.REPORT_GENERATOR_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrganizationEmailServiceTest extends AbstractServiceTest {

    @Autowired
    protected MessageUtil messageUtil;

    @Autowired
    private OrganizationEmailService emailService;

    private static Validator validator;

    static {
        Configuration<?> config = Validation.byDefaultProvider().configure();
        ValidatorFactory factory = config.buildValidatorFactory();
        validator = factory.getValidator();
        factory.close();
    }

    @Test
    void getEmail() {
        var email = emailService.get(EMAIL_3.id(), REPORT_GENERATOR_2.id());
        ORG_EMAIL_MATCHERS.assertMatch(email, EMAIL_3);
    }

    @Test
    void getNotExistingEmail() {
        assertThrows(NotFoundException.class, () -> emailService.get(101, REPORT_GENERATOR_2.id()));
    }

    @Test
    void deleteEmail() {
        emailService.delete(EMAIL_2.id(), REPORT_GENERATOR_1.id());
        assertThrows(NotFoundException.class, () -> emailService.delete(EMAIL_2.id(), REPORT_GENERATOR_1.id()));
    }

    @Test
    void deleteNotExistingUser() {
        assertThrows(NotFoundException.class, () -> emailService.delete(102, REPORT_GENERATOR_1.id()));
    }

    @Test
    void getAllByReportGeneratorId() {
        var emailList = emailService.getAllByReportGeneratorId(REPORT_GENERATOR_1.id());
        ORG_EMAIL_MATCHERS.assertMatch(emailList, EMAIL_1, EMAIL_2);
    }

    @Test
    void updateEmail() {
        var updated = emailService.get(EMAIL_3.id(), REPORT_GENERATOR_2.id());
        updated.setEmail("update@mail.com");
        emailService.update(updated, REPORT_GENERATOR_2.id());
        ORG_EMAIL_MATCHERS.assertMatch(emailService.get(EMAIL_3.id(), REPORT_GENERATOR_2.id()), updated);
    }

    @Test
    void createNewEmail() {
        var newEmail = new OrganizationEmail(null, "mail123@mail.com");
        var created = emailService.create(newEmail, REPORT_GENERATOR_1.id());
        var newId = created.getId();
        newEmail.setId(newId);
        ORG_EMAIL_MATCHERS.assertMatch(created, newEmail);
        ORG_EMAIL_MATCHERS.assertMatch(emailService.get(newId, REPORT_GENERATOR_1.id()), newEmail);
    }

    @Test
    void createDuplicateEmail() {
        assertThrows(DataAccessException.class, () ->
                emailService.create(new OrganizationEmail(null, EMAIL_1.getEmail()), REPORT_GENERATOR_1.id()));
    }

    @Test
    void createNewReportGeneratorWithWrongContractTypeLength() {
        OrganizationEmail organizationEmail = new OrganizationEmail(null, "@mail.com");

        Set<ConstraintViolation<OrganizationEmail>> violations =
                validator.validate(organizationEmail, ErrorSequence.Second.class);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(getMessageByCode(action
                .getMessage()
                .replaceAll("[{}]", "")))
                .isEqualTo(getMessageByCode("organizationEmail.email.validEmail")));
    }

    private String getMessageByCode(String code) {
        return messageUtil.getMessage(code, MessageUtil.RU_LOCALE);
    }
}