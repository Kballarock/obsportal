package by.obs.portal.web.controller.user;

import by.obs.portal.persistence.model.user.Privilege;
import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.service.RoleService;
import by.obs.portal.utils.UriUtil;
import by.obs.portal.validation.view.ErrorSequence;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static by.obs.portal.utils.ValidationUtil.assureIdConsistent;

@RestController
@RequestMapping(value = RoleRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoleRestController {

    static final String REST_URL = "/rest/admin/roles";
    RoleService roleService;

    @Autowired
    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<Role> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    public Role get(@PathVariable int id) {
        return roleService.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Role> createWithLocation(@Validated(ErrorSequence.class) @RequestBody Role role) {
        Role created = roleService.create(role);

        URI uriOfNewResource = UriUtil.createNewURI(REST_URL, created.getId());

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        roleService.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(ErrorSequence.class) @RequestBody Role role, @PathVariable int id) {
        assureIdConsistent(role, id);
        roleService.update(role);
    }

    @GetMapping(value = "/{id}/privileges")
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