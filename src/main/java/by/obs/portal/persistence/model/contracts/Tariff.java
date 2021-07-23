package by.obs.portal.persistence.model.contracts;

import by.obs.portal.persistence.model.base.AbstractNamedEntity;
import by.obs.portal.validation.view.ErrorSequence;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "tariff")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tariff extends AbstractNamedEntity {

    @NotNull
    @Column(name = "category", nullable = false)
    Integer category;

    @NotNull
    @JsonIgnore
    @Column(name = "u_min", nullable = false)
    Integer minUsers;

    @NotNull
    @JsonIgnore
    @Column(name = "u_max", nullable = false)
    Integer maxUsers;

    @NotNull(message = "{tariff.price.notNull}", groups = ErrorSequence.First.class)
    @DecimalMin(value = "0.01", message = "{tariff.price.decimalMin}", groups = ErrorSequence.Second.class)
    @Column(name = "price", nullable = false)
    Double price;

    @NotEmpty
    @Column(name = "description", nullable = false)
    String description;

    public Tariff(Integer id, String name, int category, int minUsers, int maxUsers, double price, String description) {
        super(id, name);
        this.category = category;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
        this.price = price;
        this.description = description;
    }
}