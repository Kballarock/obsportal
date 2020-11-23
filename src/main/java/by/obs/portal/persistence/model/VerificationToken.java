package by.obs.portal.persistence.model;

import by.obs.portal.persistence.model.base.AbstractBaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "verification_token")
public class VerificationToken extends AbstractBaseEntity {

    static final int VERIFICATION_TOKEN_EXPIRATION = 24;
    LocalDateTime expiryDate;
    String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    User user;

    private VerificationToken(final int id, final String token, LocalDateTime expiryDate, User user) {
        super(id);
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public VerificationToken(final String token, final User user) {
        super();
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(VERIFICATION_TOKEN_EXPIRATION);
    }

    public VerificationToken(VerificationToken token) {
        this(token.getId(), token.getToken(), token.getExpiryDate(), token.getUser());
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusHours(VERIFICATION_TOKEN_EXPIRATION);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationToken that = (VerificationToken) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getToken(), that.getToken()) &&
                Objects.equals(getExpiryDate(), that.getExpiryDate()) &&
                Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getToken(), getExpiryDate(), getUser());
    }
}