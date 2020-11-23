package by.obs.portal.validation;

import by.obs.portal.persistence.repository.UserRepository;
import by.obs.portal.utils.user.HasEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static by.obs.portal.web.exception.ExceptionInfoHandler.EXCEPTION_DUPLICATE_EMAIL;

@Component
public class UniqueMailValidator implements org.springframework.validation.Validator {

    private final UserRepository userRepository;

    @Autowired
    public UniqueMailValidator(UserRepository repository) {
        this.userRepository = repository;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return HasEmail.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        HasEmail user = ((HasEmail) target);
        var dbUser = userRepository.getByEmail(user.getEmail().toLowerCase());
        if (dbUser != null && !dbUser.getId().equals(user.getId())) {
            errors.rejectValue("email", EXCEPTION_DUPLICATE_EMAIL);
        }
    }
}