package by.obs.portal.service;

import by.obs.portal.persistence.model.VerificationToken;
import by.obs.portal.persistence.repository.VerificationTokenRepository;
import by.obs.portal.utils.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static by.obs.portal.common.Constants.*;
import static by.obs.portal.testdata.UserTestData.*;
import static by.obs.portal.testdata.UserTestData.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@FieldDefaults(level = AccessLevel.PRIVATE)
class VerificationTokenServiceTest extends AbstractServiceTest {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    VerificationTokenService verificationTokenService;

    @Autowired
    UserService userService;

    @Test
    void getUserByVerificationToken() {
        var newUser = getNew();
        var createdUser = userService.create(newUser);
        var newId = createdUser.getId();
        newUser.setId(newId);
        var token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(createdUser, token);
        USER_MATCHERS.assertMatch(verificationTokenService.getByVerificationToken(token), createdUser);
    }

    @Test
    void getUserByNotExistingVerificationToken() {
        var token = UUID.randomUUID().toString();
        assertThrows(NotFoundException.class, () -> verificationTokenService.getByVerificationToken(token));
    }

    @Test
    void getVerificationToken() {
        var newUser = getNew();
        var createdUser = userService.create(newUser);
        var token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(createdUser, token);
        var verificationToken = verificationTokenService.getVerificationToken(token);
        assertNotNull(verificationToken);
        assertEquals(createdUser, verificationToken.getUser());
        assertEquals(createdUser.getId(), verificationToken.getUser().getId());
        assertEquals(token, verificationToken.getToken());
    }

    @Test
    void getNotExistingVerificationToken() {
        var token = UUID.randomUUID().toString();
        assertThrows(NotFoundException.class, () -> verificationTokenService.getVerificationToken(token));
    }

    @Test
    void createNewVerificationToken() {
        var user = getNew();
        var createdUser = userService.create(user);
        var token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(createdUser, token);
        var verificationToken = verificationTokenService.getVerificationToken(token);
        assertNotNull(verificationToken);
        assertNotNull(verificationToken.getId());
        assertNotNull(verificationToken.getUser());
        assertEquals(user, verificationToken.getUser());
        assertEquals(user.getId(), verificationToken.getUser().getId());
        assertEquals(token, verificationToken.getToken());
        assertTrue(verificationToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void createDuplicateVerificationToken() {
        var newUser = getNew();
        var createdUser = userService.create(newUser);
        var token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(createdUser, token);
        verificationTokenService.createVerificationToken(createdUser, token);
    }

    @Test
    void deleteVerificationTokenById() {
        var token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(USER, token);
        var tokenId = verificationTokenService.getVerificationToken(token).getId();
        verificationTokenService.deleteVerificationTokenById(tokenId);
        assertThrows(NotFoundException.class, () -> verificationTokenService.getVerificationToken(token));
    }

    @Test
    void deleteNotExistingVerificationTokenById() {
        assertThrows(NotFoundException.class, () -> verificationTokenService.deleteVerificationTokenById(456));
    }

    @Test
    void generateNewVerificationToken() {
        var user = getNew();
        userService.create(user);
        var token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);
        var originalToken = new VerificationToken(verificationTokenService.getVerificationToken(token));
        var newToken = verificationTokenService.generateNewVerificationToken(token);
        assertNotEquals(newToken.getToken(), originalToken.getToken());
        assertNotEquals(newToken, originalToken);
    }

    @Test
    void enabledUserAfterVerificationTokenValidation() {
        var user = getNew();
        userService.create(user);
        var token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);
        var userId = user.id();
        var tokenStatus = verificationTokenService.validateVerificationToken(token);
        assertEquals(tokenStatus, TOKEN_VALID);
        user = userService.get(userId);
        assertTrue(user.isEnabled());
    }

    @Test
    void tokenVerificationValidationIfTokenIsInvalid() {
        var user = getNew();
        userService.create(user);
        var token = UUID.randomUUID().toString();
        final String invalid_token = "INVALID_" + UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);
        final String token_status = verificationTokenService.validateVerificationToken(invalid_token);
        assertEquals(token_status, TOKEN_INVALID);
    }

    @Test
    void tokenVerificationValidationIfTokenIsExpired() {
        var user = getNew();
        userService.create(user);
        var token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);
        var verificationToken = verificationTokenService.getVerificationToken(token);
        verificationToken.setExpiryDate(LocalDateTime.of(2020, Month.MAY, 15, 0, 0));
        verificationTokenRepository.save(verificationToken);
        final String token_status = verificationTokenService.validateVerificationToken(token);
        assertNotNull(token_status);
        assertEquals(token_status, TOKEN_EXPIRED);
    }
}