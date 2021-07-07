package by.obs.portal.web.controller.contract;

import by.obs.portal.persistence.model.contracts.Tariff;
import by.obs.portal.service.TariffService;
import by.obs.portal.web.controller.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static by.obs.portal.testdata.TariffTestData.*;
import static by.obs.portal.testdata.UserTestData.ADMIN;
import static by.obs.portal.testdata.UserTestData.USER;
import static by.obs.portal.utils.exception.ErrorType.VALIDATION_ERROR;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TariffRestControllerTest extends AbstractControllerTest {

    @Autowired
    private TariffService tariffService;

    TariffRestControllerTest() {
        super(TariffRestController.REST_URL);
    }

    @Test
    void getAll() throws Exception {
        perform(doGet()
                .basicAuth(ADMIN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TARIFF_MATCHERS.contentJson(TARIFFS));
    }

    @Test
    void get() throws Exception {
        perform(doGet(TARIFF_2.id())
                .basicAuth(ADMIN))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TARIFF_MATCHERS.contentJson(TARIFF_2));
    }

    @Test
    void getNotFound() throws Exception {
        perform(doGet(123456)
                .basicAuth(ADMIN))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void getUnAuth() throws Exception {
        perform(doGet())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getForbidden() throws Exception {
        perform(doGet()
                .basicAuth(USER))
                .andExpect(status().isForbidden());
    }

    @Test
    void update() throws Exception {
        Tariff updated = TARIFF_1;
        updated.setPrice(45.00);
        perform(doPut(TARIFF_1.id())
                .jsonBody(updated)
                .basicAuth(ADMIN))
                .andExpect(status().isNoContent());

        TARIFF_MATCHERS.assertMatch(tariffService.get(TARIFF_1.id()), updated);
    }

    @Test
    void updateInvalid() throws Exception {
        Tariff updated = TARIFF_1;
        updated.setPrice(0.00);
        perform(doPut(TARIFF_1.id()).jsonBody(updated).basicAuth(ADMIN))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }
}