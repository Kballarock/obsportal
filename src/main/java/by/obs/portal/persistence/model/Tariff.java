package by.obs.portal.persistence.model;

import by.obs.portal.persistence.model.base.AbstractNamedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tariff extends AbstractNamedEntity {

    @NotNull
    @Column(name = "category", nullable = false)
    int category;

    @NotNull
    @JsonIgnore
    @Column(name = "u_min", nullable = false)
    int minUsers;

    @NotNull
    @JsonIgnore
    @Column(name = "u_max", nullable = false)
    int maxUsers;

    @NotNull
    @DecimalMin(value = "0.01", message = "{tariff.price.decimalMin}")
    @Column(name = "price", nullable = false)
    double price;

    @NotEmpty
    @Column(name = "description", nullable = false)
    String description;
}