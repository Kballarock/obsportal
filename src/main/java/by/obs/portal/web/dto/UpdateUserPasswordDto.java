package by.obs.portal.web.dto;

import by.obs.portal.validation.PasswordMatches;
import by.obs.portal.validation.view.ErrorSequence;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@PasswordMatches(first = "newPassword", second = "confirmNewPassword", groups = ErrorSequence.Third.class)
public class UpdateUserPasswordDto {

    String oldPassword;

    @NotBlank(message = "{updateUserPasswordDto.password.notBlank}", groups = ErrorSequence.First.class)
    @Size(min = 8, max = 100, message = "{updateUserPasswordDto.password.size}", groups = ErrorSequence.Second.class)
    String newPassword;

    String confirmNewPassword;

    public UpdateUserPasswordDto(String newPassword, String confirmNewPassword) {
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}