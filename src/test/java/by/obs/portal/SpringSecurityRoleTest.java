package by.obs.portal;

import by.obs.portal.persistence.model.user.Privilege;
import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.persistence.model.user.User;
import by.obs.portal.persistence.repository.PrivilegeRepository;
import by.obs.portal.persistence.repository.RoleRepository;
import by.obs.portal.service.UserService;
import by.obs.portal.utils.TimingExtension;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static by.obs.portal.testdata.UserTestData.getNew;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(TimingExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
public class SpringSecurityRoleTest {

    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PrivilegeRepository privilegeRepository;

    User user;
    Role role;
    Privilege privilege;

    @Test
    public void testDeleteUser() {
        role = new Role("TEST_ROLE");
        roleRepository.create(role);

        user = getNew();
        user.setRoles(Set.of(role));
        user.setEnabled(true);
        userService.create(user);

        assertNotNull(userService.getByEmail(user.getEmail()));
        assertNotNull(roleRepository.getByName(role.getName()));
        user.setRoles(null);
        userService.delete(user.id());

        assertNull(userService.getByEmail(user.getEmail()));
        assertNotNull(roleRepository.getByName(role.getName()));
    }

    @Test
    public void testDeleteRole() {
        privilege = new Privilege("TEST_PRIVILEGE");
        privilegeRepository.create(privilege);

        role = new Role("TEST_ROLE");
        role.setPrivileges(Collections.singletonList(privilege));
        roleRepository.create(role);

        user = getNew();
        user.setRoles(Set.of(role));
        user.setEnabled(true);
        userService.create(user);

        assertNotNull(privilegeRepository.getByName(privilege.getName()));
        assertNotNull(userService.getByEmail(user.getEmail()));
        assertNotNull(roleRepository.getByName(role.getName()));

        user.setRoles(new HashSet<>());
        role.setPrivileges(new ArrayList<>());
        roleRepository.delete(role);

        assertNull(roleRepository.getByName(role.getName()));
        assertNotNull(privilegeRepository.getByName(privilege.getName()));
        assertNotNull(userService.getByEmail(user.getEmail()));
    }

    @Test
    public void testDeletePrivilege() {
        privilege = new Privilege("TEST_PRIVILEGE");
        privilegeRepository.create(privilege);

        role = new Role("TEST_ROLE");
        role.setPrivileges(Collections.singletonList(privilege));
        roleRepository.create(role);

        assertNotNull(roleRepository.getByName(role.getName()));
        assertNotNull(privilegeRepository.getByName(privilege.getName()));

        role.setPrivileges(new ArrayList<>());
        privilegeRepository.delete(privilege);

        assertNull(privilegeRepository.getByName(privilege.getName()));
        assertNotNull(roleRepository.getByName(role.getName()));
    }
}