package by.obs.portal.web.controller.user;

import by.obs.portal.persistence.model.user.Privilege;
import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.service.RoleService;
import by.obs.portal.validation.view.ErrorSequence;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax/admin/roles")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoleUIController {

    RoleService roleService;

    @Autowired
    public RoleUIController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Role> getAll() {
        return roleService.getAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Role get(@PathVariable int id) {
        return roleService.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        roleService.delete(id);
    }

    @PostMapping
    public void createOrUpdate(@Validated(ErrorSequence.class) Role role) {
        if (role.isNew()) {
            roleService.create(role);
        } else {
            roleService.update(role);
        }
    }

    @GetMapping(value = "/{id}/privileges", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Privilege> getAllPrivilege(@PathVariable("id") int id) {
        return (List<Privilege>) roleService.get(id).getPrivileges();
    }

    @DeleteMapping(value = "/{id}/privileges/{privilegeId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id, @PathVariable("privilegeId") int privilegeId) {
        roleService.deletePrivilege(id, privilegeId);
    }

    @PostMapping(value = "/{id}/privileges/{privilegeId}")
    public void createOrUpdate(@PathVariable("id") int id, @PathVariable("privilegeId") int privilegeId) {
        roleService.addPrivilege(id, privilegeId);
    }
}