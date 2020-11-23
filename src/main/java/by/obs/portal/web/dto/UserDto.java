package by.obs.portal.web.dto;

import by.obs.portal.utils.user.HasEmail;
import by.obs.portal.validation.PasswordMatches;
import by.obs.portal.validation.ValidEmail;
import by.obs.portal.validation.view.ErrorSequence;
import by.obs.portal.web.dto.base.AbstractBaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PasswordMatches(first = "password", second = "matchingPassword", groups = ErrorSequence.Third.class)
public class UserDto extends AbstractBaseEntity implements HasEmail {

    @NotBlank(message = "{userDto.name.notBlank}", groups = ErrorSequence.First.class)
    @Size(min = 3, max = 120, message = "{userDto.name.size}", groups = ErrorSequence.Second.class)
    String name;

    @NotBlank(message = "{userDto.email.notBlank}", groups = ErrorSequence.First.class)
    @ValidEmail(message = "{userDto.email.validEmail}", groups = ErrorSequence.Second.class)
    String email;

    @ToString.Exclude
    @NotBlank(message = "{userDto.password.notBlank}", groups = ErrorSequence.First.class)
    @Size(min = 8, max = 100, message = "{userDto.password.size}", groups = ErrorSequence.Second.class)
    String password;

    @ToString.Exclude
    @NotBlank(message = "{userDto.matchingPassword.notBlank}", groups = ErrorSequence.First.class)
    String matchingPassword;
}