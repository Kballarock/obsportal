package by.obs.portal.testdata;

import by.obs.portal.persistence.model.contracts.OrganizationEmail;
import by.obs.portal.utils.TestMatchers;

public class OrganizationEmailTestData {

    public static final OrganizationEmail EMAIL_1 = new OrganizationEmail(1, "sol@sol.by");
    public static final OrganizationEmail EMAIL_2 = new OrganizationEmail(2, "solig@solig.by");
    public static final OrganizationEmail EMAIL_3 = new OrganizationEmail(3, "vit@vit.by");

    public static TestMatchers<OrganizationEmail> ORG_EMAIL_MATCHERS =
            TestMatchers.useFieldsComparator(OrganizationEmail.class, "reportGenerator");
}