package by.obs.portal.persistence.model;

import by.obs.portal.persistence.model.base.AbstractBaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "password_reset_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetToken extends AbstractBaseEntity {

    static final int PASSWORD_RESET_TOKEN_EXPIRATION = 24;
    String token;
    LocalDateTime expiryDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    User user;

    public PasswordResetToken(final String token, final User user) {
        super();
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(PASSWORD_RESET_TOKEN_EXPIRATION);
    }
}