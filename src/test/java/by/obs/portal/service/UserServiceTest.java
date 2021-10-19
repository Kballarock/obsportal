package by.obs.portal.service;

import by.obs.portal.persistence.model.user.AuthProvider;
import by.obs.portal.persistence.model.user.User;
import by.obs.portal.utils.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

import static by.obs.portal.testdata.UserTestData.*;
import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceTest extends AbstractServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Test
    void createNewUser() {
        var newUser = getNew();
        var created = userService.create(newUser);
        var newId = created.getId();
        newUser.setId(newId);
        USER_MATCHERS.assertMatch(created, newUser);
        USER_MATCHERS.assertMatch(userService.get(newId), newUser);
    }

    @Test
    void createNewUserWithDuplicateEmail() {
        assertThrows(DataAccessException.class, () ->
                userService.create(new User(null, "DuplicateUser", "user@user.com",
                        "newPassword", AuthProvider.local, null, Set.of(ROLE_USER))));
    }

    @Test
    void deleteUser() {
        userService.delete(USER.getId());
        assertThrows(NotFoundException.class, () -> userService.delete(USER.getId()));
    }

    @Test
    void deleteNotExistingUser() {
        assertThrows(NotFoundException.class, () -> userService.delete(100));
    }

    @Test
    void getUser() {
        var user = userService.get(ADMIN.getId());
        USER_MATCHERS.assertMatch(user, ADMIN);
    }

    @Test
    void getNotExistingUser() {
        assertThrows(NotFoundException.class, () -> userService.get(1));
    }

    @Test
    void getUserByEmail() {
        var user = userService.getByEmail("admin@admin.com");
        USER_MATCHERS.assertMatch(user, ADMIN);
    }

    @Test
    void getAllUsers() {
        var allUsers = userService.getAll();
        USER_MATCHERS.assertMatch(allUsers, ADMIN, USER);
    }

    @Test
    void updateUser() {
        var updated = getUpdated();
        userService.update(updated);
        USER_MATCHERS.assertMatch(userService.get(USER.getId()), updated);
    }

    @Test
    void enableUser() {
        userService.enable(USER.getId(), false);
        assertFalse(userService.get(USER.getId()).isEnabled());
        userService.enable(USER.getId(), true);
        assertTrue(userService.get(USER.getId()).isEnabled());
    }

    @Test
    void createNewUserWithWrongData() {
        validateRootCause(() -> userService.create(
                new User(null, " ", "mail@mail.ru", "password",
                        AuthProvider.local, null, Set.of(ROLE_USER))), ConstraintViolationException.class);

        validateRootCause(() -> userService.create(
                new User(null, "User", "  ", "password",
                        AuthProvider.local, null, Set.of(ROLE_USER))), ConstraintViolationException.class);

        validateRootCause(() -> userService.create(
                new User(null, "User", "mail@mail.ru", "  ",
                        AuthProvider.local, null, Set.of(ROLE_USER))), ConstraintViolationException.class);

        validateRootCause(() -> userService.create(
                new User(null, "User", "mailmail.ru", "password",
                        AuthProvider.local, null, Set.of(ROLE_USER))), ConstraintViolationException.class);

        validateRootCause(() -> userService.create(
                new User(null, "User", "mail@", "password",
                        AuthProvider.local, null, Set.of(ROLE_USER))), ConstraintViolationException.class);
    }

    @Test
    void updateUserPassword() {
        userService.updatePassword(ADMIN, "newUserPassword");
        var updatedPassword = userService.get(ADMIN.id()).getPassword();
        passwordEncoder.matches("newUserPassword", updatedPassword);
    }

    @Test
    void getUserRolesById() {
        var userRoles = userService.getUserRolesById(ADMIN.id());
        assertEquals(new HashSet<>(userRoles), new HashSet<>(ADMIN.getRoles()));
    }

    @Test
    void getWrongUserRolesById() {
        var userRoles = userService.getUserRolesById(USER.id());
        assertNotEquals(new HashSet<>(userRoles), new HashSet<>(ADMIN.getRoles()));
    }

    @Test
    void enableUserRole() {
        userService.enableUserRole(ADMIN.id(), ROLE_REPORT_GENERATOR.id());
        var userRoles = userService.getUserRolesById(ADMIN.id());
        assertEquals(new HashSet<>(userRoles), new HashSet<>(ADMIN_ADD_NEW_ROLE));
    }

    @Test
    void disableUserRole() {
        userService.disableUserRole(USER.id(), ROLE_USER.id());
        assertThrows(NotFoundException.class, () -> userService.disableUserRole(USER.id(), ROLE_USER.id()));
    }

    @Test
    void disableUserRoleNotExisting() {
        assertThrows(NotFoundException.class, () -> userService.disableUserRole(USER.id(), 1547852));
    }
}