package by.obs.portal.web.controller.user;

import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.persistence.model.user.User;
import by.obs.portal.service.UserService;
import by.obs.portal.utils.user.UserUtil;
import by.obs.portal.validation.view.ErrorSequence;
import by.obs.portal.web.dto.UserDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/ajax/admin/users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AdminUIController {

    UserService userService;

    @Autowired
    public AdminUIController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(@PathVariable int id) {
        return userService.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @PostMapping
    public void createOrUpdate(@Validated(ErrorSequence.class) UserDto userDto) {
        if (userDto.isNew()) {
            userService.create(UserUtil.createNewFromDto(userDto));
        } else {
            userService.update(userDto);
            userService.enable(userDto.getId(), true);
        }
    }

    @PostMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        userService.enable(id, enabled);
    }

    @GetMapping(value = "/{id}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<Role> getUserRoles(@PathVariable int id) {
        return userService.getUserRolesById(id);
    }

    @PostMapping("/{id}/roles/{roleId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enableUserRole(@PathVariable("id") int id, @PathVariable("roleId") int roleId) {
        userService.enableUserRole(id, roleId);
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void disableUserRole(@PathVariable("id") int id, @PathVariable("roleId") int roleId) {
        userService.disableUserRole(id, roleId);
    }
}