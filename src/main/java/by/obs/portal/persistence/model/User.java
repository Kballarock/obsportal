package by.obs.portal.persistence.model;

import by.obs.portal.common.Constants;
import by.obs.portal.persistence.model.base.AbstractNamedEntity;
import by.obs.portal.utils.user.HasEmail;
import by.obs.portal.validation.ValidEmail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "users_unique_email_idx")})
public class User extends AbstractNamedEntity implements HasEmail {

    @NotBlank
    @ValidEmail
    String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    String password;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime registered = LocalDateTime.now();

    boolean enabled = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    AuthProvider provider;

    @Column(name = "provider_id")
    String providerId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    Set<Role> roles;

    public User(User u) {
        this(u.getId(), u.getName(), u.getEmail(), u.getPassword(), u.isEnabled(),
                u.getRegistered(), u.getProvider(), u.getProviderId(), u.getRoles());
    }

    public User(Integer id, String name, String email, String password, boolean enabled, LocalDateTime registered,
                AuthProvider provider, String providerId, Set<Role> roles) {
        super(id, name);
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.registered = registered;
        this.provider = provider;
        this.providerId = providerId;
        setRoles(roles);
    }

    public User(Integer id, String name, String email, String password, AuthProvider provider, String providerId, Set<Role> roles) {
        this(id, name, email, password, true, LocalDateTime.now(), provider, providerId, roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(provider, that.provider) &&
                Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), email, password, roles);
    }
}