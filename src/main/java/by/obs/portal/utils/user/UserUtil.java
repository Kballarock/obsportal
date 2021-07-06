package by.obs.portal.utils.user;

import by.obs.portal.persistence.model.user.AuthProvider;
import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.persistence.model.user.User;
import by.obs.portal.persistence.repository.RoleRepository;
import by.obs.portal.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserUtil {

    private static RoleRepository roleRepository;

    @Autowired
    public UserUtil(RoleRepository roleRepository) {
        UserUtil.roleRepository = roleRepository;
    }

    public static User createNewFromDto(UserDto userDto) {
        Role userRole = roleRepository.getByName("ROLE_USER");
        Set<Role> roles = new HashSet<>();

        if (userRole != null) {
            roles.add(userRole);
        }
        return new User(null, userDto.getName(), userDto.getEmail(), userDto.getPassword(),
                AuthProvider.local, null, roles);
    }

    public static User updateFromDto(User user, UserDto userDto) {
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail().toLowerCase());
        user.setPassword(userDto.getPassword());
        return user;
    }

    public static User prepareToSave(User user, BCryptPasswordEncoder passwordEncoder) {
        String password = user.getPassword();
        user.setPassword(StringUtils.hasText(password) ? passwordEncoder.encode(password) : password);
        user.setEmail(user.getEmail().toLowerCase());
        user.setEnabled(false);
        return user;
    }
}