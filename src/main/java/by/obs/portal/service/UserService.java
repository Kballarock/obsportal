package by.obs.portal.service;

import by.obs.portal.persistence.model.user.User;
import by.obs.portal.persistence.repository.UserRepository;
import by.obs.portal.utils.user.UserUtil;
import by.obs.portal.web.dto.UserDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

import static by.obs.portal.utils.user.UserUtil.prepareToSave;
import static by.obs.portal.utils.ValidationUtil.checkNotFoundWithId;

@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {

    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
    }

    public User create(final User user) {
        Assert.notNull(user, "User must not be null");
        return prepareAndSave(user);
    }

    public void delete(final int id) {
        checkNotFoundWithId(userRepository.delete(id), id);
    }

    public User get(final int id) {
        return checkNotFoundWithId(userRepository.get(id), id);
    }

    public User getByEmail(final String email) {
        Assert.notNull(email, "Email must not be null");
        return userRepository.getByEmail(email);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public void saveRegisteredUser(final User user) {
        Assert.notNull(user, "User must not be null");
        userRepository.save(user);
    }

    public void update(final User user) {
        Assert.notNull(user, "User must not be null");
        prepareAndSave(user);
    }

    public void update(final UserDto userDto) {
        User user = get(userDto.id());
        prepareAndSave(UserUtil.updateFromDto(user, userDto));
    }

    public void enable(final int id, final boolean enabled) {
        var user = get(id);
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    public void updatePassword(final User user, final String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public boolean validOldPassword(final User user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    private User prepareAndSave(User user) {
        return userRepository.save(prepareToSave(user, passwordEncoder));
    }
}