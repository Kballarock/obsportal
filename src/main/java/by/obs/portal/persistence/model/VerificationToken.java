package by.obs.portal.persistence.model;

import by.obs.portal.persistence.model.base.AbstractBaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

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
        if (!(o instanceof VerificationToken)) return false;
        if (!super.equals(o)) return false;

        VerificationToken that = (VerificationToken) o;

        if (!getToken().equals(that.getToken())) return false;
        return getUser().equals(that.getUser());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getToken().hashCode();
        result = 31 * result + getUser().hashCode();
        return result;
    }
}