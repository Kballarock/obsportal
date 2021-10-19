package by.obs.portal.service;

import by.obs.portal.persistence.model.contracts.ReportGenerator;
import by.obs.portal.utils.exception.MessageUtil;
import by.obs.portal.utils.exception.NotFoundException;
import by.obs.portal.validation.view.ErrorSequence;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.*;
import javax.validation.groups.Default;
import java.time.LocalDate;
import java.util.Set;

import static by.obs.portal.testdata.ReportGeneratorTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReportGeneratorServiceTest extends AbstractServiceTest {

    @Autowired
    protected MessageUtil messageUtil;

    @Autowired
    private ReportGeneratorService reportGeneratorService;

    private static Validator validator;

    static {
        Configuration<?> config = Validation.byDefaultProvider().configure();
        ValidatorFactory factory = config.buildValidatorFactory();
        validator = factory.getValidator();
        factory.close();
    }

    @Test
    void createNewReportGenerator() {
        var newRepGen = getNew();
        var created = reportGeneratorService.create(newRepGen);
        var newId = created.getId();
        newRepGen.setId(newId);
        REPORT_GENERATOR_MATCHERS.assertMatch(created, newRepGen);
        REPORT_GENERATOR_MATCHERS.assertMatch(reportGeneratorService.get(newId), newRepGen);
    }

    @Test
    void deleteReportGenerator() {
        reportGeneratorService.delete(REPORT_GENERATOR_1.getId());
        assertThrows(NotFoundException.class, () -> reportGeneratorService.delete(REPORT_GENERATOR_1.getId()));
    }

    @Test
    void deleteNotExistingReportGenerator() {
        assertThrows(NotFoundException.class, () -> reportGeneratorService.delete(1524));
    }

    @Test
    void getReportGenerator() {
        var repGen = reportGeneratorService.get(REPORT_GENERATOR_2.getId());
        REPORT_GENERATOR_MATCHERS.assertMatch(repGen, REPORT_GENERATOR_2);
    }

    @Test
    void getNotExistingReportGenerator() {
        assertThrows(NotFoundException.class, () -> reportGeneratorService.get(15689));
    }

    @Test
    @Order(1)
    void getAllReportGenerators() {
        var allRepGens = reportGeneratorService.getAll();
        REPORT_GENERATOR_MATCHERS.assertMatch(allRepGens, REPORT_GENERATOR_1, REPORT_GENERATOR_2);
    }

    @Test
    void updateReportGenerator() {
        var updated = getUpdated();
        reportGeneratorService.update(updated);
        REPORT_GENERATOR_MATCHERS.assertMatch(reportGeneratorService.get(REPORT_GENERATOR_1.getId()), updated);
    }

    @Test
    void createNewReportGeneratorWithWrongContractName() {
        ReportGenerator reportGenerator = new ReportGenerator(null, " ", "6ГО",
                111, LocalDate.now(), 111111111, 4);

        Set<ConstraintViolation<ReportGenerator>> violations =
                validator.validate(reportGenerator, ErrorSequence.First.class);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(getMessageByCode(action
                .getMessage()
                .replaceAll("[{}]", "")))
                .isEqualTo(getMessageByCode("abstractNamedEntity.NotBlank.name")));
    }

    @Test
    void createNewReportGeneratorWithWrongContractType() {
        ReportGenerator reportGenerator = new ReportGenerator(null, "newOrg", " ",
                111, LocalDate.now(), 111111111, 4);

        Set<ConstraintViolation<ReportGenerator>> violations =
                validator.validate(reportGenerator, ErrorSequence.First.class);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(getMessageByCode(action
                .getMessage()
                .replaceAll("[{}]", "")))
                .isEqualTo(getMessageByCode("reportGenerator.contractType.notBlank")));
    }

    @Test
    void createNewReportGeneratorWithWrongContractTypeLength() {
        ReportGenerator reportGenerator = new ReportGenerator(null, "newOrg", "34ГО-3454664543",
                111, LocalDate.now(), 111111111, 4);

        Set<ConstraintViolation<ReportGenerator>> violations =
                validator.validate(reportGenerator, ErrorSequence.Second.class);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(getMessageByCode(action
                .getMessage()
                .replaceAll("[{}]", "")))
                .isEqualTo(getMessageByCode("reportGenerator.contractType.size")));
    }

    @Test
    void createNewReportGeneratorWithWrongContractNumber() {
        ReportGenerator reportGenerator = new ReportGenerator(null, "newOrg", "34ГО",
                0, LocalDate.now(), 111111111, 4);

        Set<ConstraintViolation<ReportGenerator>> violations =
                validator.validate(reportGenerator, ErrorSequence.Second.class);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(getMessageByCode(action
                .getMessage()
                .replaceAll("[{}]", "")))
                .isEqualTo(getMessageByCode("reportGenerator.contractNumber.min")));
    }

    @Test
    void createNewReportGeneratorWithWrongContractUnpRage() {
        ReportGenerator reportGenerator = new ReportGenerator(null, "newOrg", "34ГО",
                111, LocalDate.now(), 1111111111, 4);

        Set<ConstraintViolation<ReportGenerator>> violations =
                validator.validate(reportGenerator, ErrorSequence.Second.class);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(getMessageByCode(action
                .getMessage()
                .replaceAll("[{}]", "")))
                .isEqualTo(getMessageByCode("reportGenerator.unp.range")));
    }

    @Test
    void createNewReportGeneratorWithWrongContractUserAmount() {
        ReportGenerator reportGenerator = new ReportGenerator(null, "newOrg", "34ГО",
                111, LocalDate.now(), 111111111, 0);

        Set<ConstraintViolation<ReportGenerator>> violations =
                validator.validate(reportGenerator, ErrorSequence.Second.class);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(getMessageByCode(action
                .getMessage()
                .replaceAll("[{}]", "")))
                .isEqualTo(getMessageByCode("reportGenerator.usersAmount.min")));
    }

    private String getMessageByCode(String code) {
        return messageUtil.getMessage(code, MessageUtil.RU_LOCALE);
    }
}