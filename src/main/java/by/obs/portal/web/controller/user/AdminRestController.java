package by.obs.portal.web.controller.user;

import by.obs.portal.persistence.model.user.User;
import by.obs.portal.service.UserService;
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
@RequestMapping(value = AdminRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AdminRestController {

    static final String REST_URL = "/rest/admin/users";
    UserService userService;

    @Autowired
    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return userService.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createWithLocation(@Validated(ErrorSequence.class) @RequestBody User user) {
        User created = userService.create(user);
        userService.enable(created.id(), true);
        URI uriOfNewResource = UriUtil.createNewURI(REST_URL, created.getId());

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(ErrorSequence.class) @RequestBody User user, @PathVariable int id) {
        assureIdConsistent(user, id);
        userService.update(user);
        userService.enable(id, true);
    }

    @GetMapping("/by")
    public User getByMail(@RequestParam String email) {
        return userService.getByEmail(email);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        userService.enable(id, enabled);
    }
}