package by.obs.portal.testdata;

import by.obs.portal.persistence.model.user.AuthProvider;
import by.obs.portal.utils.TestMatchers;
import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.persistence.model.user.User;
import by.obs.portal.web.dto.UserDto;

import java.util.*;

public class UserTestData {

    private static final Role ROLE_ADMIN = new Role(1, "ROLE_ADMIN");
    public static final Role ROLE_USER = new Role(2, "ROLE_USER");
    public static final Role ROLE_REPORT_GENERATOR = new Role(3, "ROLE_REPORT_GENERATOR");

    public static final User ADMIN = new User(100000, "Admin", "admin@admin.com",
            "admin", AuthProvider.local, null, Set.of(ROLE_ADMIN));
    public static final User USER = new User(100001, "User", "user@user.com",
            "user", AuthProvider.local, null, Set.of(ROLE_USER));
    public static final UserDto USER_DTO = new UserDto("newUsername", "newusername@gmail.com",
            "password", "password");
    public static final UserDto USER_DTO_1 = new UserDto("newUsername", "newusername@tmail.com",
            "password", "password");
    public static final UserDto USER_DTO_2 = new UserDto("newUsername", "newusername@test.com",
            "password", "password");
    public static final UserDto USER_DTO_3 = new UserDto("newUsername", "newusername@yandex.com",
            "password", "password");

    public static final List<User> USERS = List.of(USER, ADMIN);
    public static final Set<Role> ADMIN_ADD_NEW_ROLE = Set.of(ROLE_REPORT_GENERATOR, ROLE_ADMIN);

    public static User getNew() {
        Set<Role> roles = new HashSet<>();
        roles.add(ROLE_USER);
        User newUser = new User(null, "NewUser", "newuser@newuser.com",
                "password", AuthProvider.local, null, roles);
        newUser.setEnabled(true);
        return newUser;
    }

    public static User getUpdated() {
        User updated = new User(USER);
        updated.setName("UpdatedUserName");
        return updated;
    }

    public static TestMatchers<User> USER_MATCHERS =
            TestMatchers.useFieldsComparator(User.class, "registered", "password");
}