package by.obs.portal.service;

import by.obs.portal.persistence.repository.PasswordResetTokenRepository;
import by.obs.portal.utils.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static by.obs.portal.testdata.UserTestData.*;
import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class PasswordResetTokenServiceTest extends AbstractServiceTest {

    @Autowired
    PasswordResetTokenService passwordResetTokenService;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    UserService userService;

    @Test
    void createPasswordResetToken() {
        var token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetToken(USER, token);
        var passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);
        assertNotNull(passwordResetToken);
        assertNotNull(passwordResetToken.getId());
        assertNotNull(passwordResetToken.getUser());
        assertEquals(USER, passwordResetToken.getUser());
        assertEquals(USER.getId(), passwordResetToken.getUser().getId());
        assertEquals(token, passwordResetToken.getToken());
        assertTrue(passwordResetToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void createDuplicatePasswordResetToken() {
        var newUser = getNew();
        var createdUser = userService.create(newUser);
        var token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetToken(createdUser, token);
        passwordResetTokenService.createPasswordResetToken(createdUser, token);
    }

    @Test
    void getPasswordResetToken() {
        var newUser = getNew();
        var createdUser = userService.create(newUser);
        var token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetToken(createdUser, token);
        var passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);
        assertNotNull(passwordResetToken);
        assertEquals(createdUser, passwordResetToken.getUser());
        assertEquals(createdUser.getId(), passwordResetToken.getUser().getId());
        assertEquals(token, passwordResetToken.getToken());
    }

    @Test
    void getNotExistingPasswordResetToken() {
        var token = UUID.randomUUID().toString();
        assertThrows(NotFoundException.class, () -> passwordResetTokenService.getPasswordResetToken(token));
    }

    @Test
    void passwordResetTokenValidationIfTokenIsInvalid() {
        var token = UUID.randomUUID().toString();
        final String invalid_token = "INVALID_" + UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetToken(USER, token);
        final String token_status = passwordResetTokenService.validatePasswordResetToken(USER.id(), invalid_token);
        assertEquals(token_status, TOKEN_INVALID);
    }

    @Test
    void passwordResetTokenValidationIfTokenIsExpired() {
        var token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetToken(USER, token);
        var passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);
        passwordResetToken.setExpiryDate(LocalDateTime.of(2020, Month.MAY, 15, 0, 0));
        passwordResetTokenRepository.save(passwordResetToken);
        final String token_status = passwordResetTokenService.validatePasswordResetToken(USER.id(), token);
        assertNotNull(token_status);
        assertEquals(token_status, TOKEN_EXPIRED);
    }
}