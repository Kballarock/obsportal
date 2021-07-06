package by.obs.portal.utils;

import by.obs.portal.persistence.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static by.obs.portal.testdata.UserTestData.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonUtilTest {

    @Test
    void readWriteValue() {
        String json = JsonUtil.writeValue(ADMIN);
        System.out.println(json);
        User user = JsonUtil.readValue(json, User.class);
        USER_MATCHERS.assertMatch(user, ADMIN);
    }

    @Test
    void readWriteValues() {
        String json = JsonUtil.writeValue(USERS);
        System.out.println(json);
        List<User> users = JsonUtil.readValues(json, User.class);
        USER_MATCHERS.assertMatch(users, USERS);
    }

    @Test
    void writeOnlyAccess() {
        String json = JsonUtil.writeValue(USER);
        System.out.println(json);
        assertThat(json, not(containsString("password")));
        String jsonWithPass = JsonUtil.writeAdditionProps(USER, "password", "newPass");
        System.out.println(jsonWithPass);
        User user = JsonUtil.readValue(jsonWithPass, User.class);
        assertEquals(user.getPassword(), "newPass");
    }
}
