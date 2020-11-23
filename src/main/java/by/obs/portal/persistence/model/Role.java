package by.obs.portal.persistence.model;

import by.obs.portal.persistence.model.base.AbstractNamedEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends AbstractNamedEntity {

    @ManyToMany
    @ToString.Exclude
    @JoinTable(name = "roles_privileges",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    Collection<Privilege> privileges;

    public Role(int id, String name) {
        super(id, name);
    }

    public Role(String name) {
        super(name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role role = (Role) obj;
        return getName().equals(role.getName());
    }
}