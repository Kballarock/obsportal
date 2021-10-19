package by.obs.portal.testdata;

import by.obs.portal.persistence.model.contracts.ReportGenerator;
import by.obs.portal.utils.TestMatchers;

import java.time.LocalDate;
import java.time.Month;

public class ReportGeneratorTestData {

    public static final ReportGenerator REPORT_GENERATOR_1 = new ReportGenerator(1, "Солигорскводоканал",
            "6ГО", 112, LocalDate.of(2020, Month.MAY, 21),
            123456789, 5);
    public static final ReportGenerator REPORT_GENERATOR_2 = new ReportGenerator(2, "Витебскводоканал",
            "2ГО", 163, LocalDate.of(2020, Month.MAY, 21),
            987654321, 10);

    public static ReportGenerator getNew() {
        return new ReportGenerator(null, "Гродноводоканал",
                "5ГО", 181, LocalDate.now(), 111111111, 6);
    }

    public static ReportGenerator getUpdated() {
        ReportGenerator updated = REPORT_GENERATOR_1;
        updated.setName("Гродноводоканал123");
        return updated;
    }

    public static TestMatchers<ReportGenerator> REPORT_GENERATOR_MATCHERS =
            TestMatchers.useFieldsComparator(ReportGenerator.class);
}