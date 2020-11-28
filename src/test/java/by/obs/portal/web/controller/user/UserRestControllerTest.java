package by.obs.portal.web.controller.user;

import by.obs.portal.persistence.model.User;
import by.obs.portal.persistence.model.VerificationToken;
import by.obs.portal.persistence.repository.VerificationTokenRepository;
import by.obs.portal.service.UserService;
import by.obs.portal.utils.user.UserUtil;
import by.obs.portal.web.controller.AbstractControllerTest;
import by.obs.portal.web.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static by.obs.portal.testdata.UserTestData.*;
import static by.obs.portal.utils.TestUtil.readFromJson;
import static by.obs.portal.utils.exception.ErrorType.VALIDATION_ERROR;
import static by.obs.portal.web.exception.ExceptionInfoHandler.EXCEPTION_DUPLICATE_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRestControllerTest extends AbstractControllerTest {

    UserRestControllerTest() {
        super(UserRestController.REST_URL);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Test
    void registerNewUserAccount() throws Exception {
        User newUser = UserUtil.createNewFromDto(USER_DTO);
        newUser.setEnabled(false);

        ResultActions action = perform(doPost("registration").jsonBody(USER_DTO))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = readFromJson(action, User.class);
        Integer newId = created.getId();
        newUser.setId(newId);
        USER_MATCHERS.assertMatch(created, newUser);
        USER_MATCHERS.assertMatch(userService.get(newId), newUser);
    }

    @Test
    void registerNewUserAccountWithDuplicateEmail() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com", "password", "password");
        User newUser = UserUtil.createNewFromDto(userDto);
        newUser.setEnabled(false);

        perform(doPost("registration").jsonBody(userDto))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL))
                .andDo(print());
    }

    @Test
    void confirmRegistrationWithValidToken() throws Exception {
        User created = registerNewUserFromDto(USER_DTO_1);
        String token = verificationTokenRepository.getByUserId(created.id()).getToken();

        perform(doGet("confirmRegistration")
                .unwrap().param("token", token))
                .andExpect(status().isOk());

        assertTrue(userService.get(created.id()).isEnabled());
    }

    @Test
    void confirmRegistrationWithInvalidToken() throws Exception {
        User created = registerNewUserFromDto(USER_DTO_2);
        String token = UUID.randomUUID().toString();

        perform(doGet("confirmRegistration")
                .unwrap().param("token", token))
                .andExpect(status().isBadRequest());

        assertFalse(userService.get(created.id()).isEnabled());
    }

    @Test
    @Transactional
    void confirmRegistrationWithExpiredToken() throws Exception {
        User created = registerNewUserFromDto(USER_DTO_3);
        VerificationToken verificationToken = verificationTokenRepository.getByUserId(created.id());
        verificationToken.setExpiryDate(verificationToken.getExpiryDate().minusHours(25));
        verificationTokenRepository.save(verificationToken);

        String token = verificationToken.getToken();

        perform(doGet("confirmRegistration")
                .unwrap().param("token", token))
                .andExpect(status().isUnprocessableEntity());

        assertFalse(userService.get(created.id()).isEnabled());
    }

    private User registerNewUserFromDto(UserDto userDto) throws Exception {
        User newUser = UserUtil.createNewFromDto(userDto);
        newUser.setEnabled(false);

        ResultActions action = perform(doPost("registration").jsonBody(userDto))
                .andDo(print())
                .andExpect(status().isCreated());

        return readFromJson(action, User.class);
    }
}