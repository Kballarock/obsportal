package by.obs.portal.persistence.model.base;

import by.obs.portal.validation.view.ErrorSequence;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class AbstractNamedEntity extends AbstractBaseEntity {

    @NotBlank(message = "{abstractNamedEntity.NotBlank.name}", groups = {ErrorSequence.First.class, Default.class})
    private String name;

    protected AbstractNamedEntity(String name) {
        this.name = name;
    }

    protected AbstractNamedEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }
}