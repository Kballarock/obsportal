package by.obs.portal.persistence.model.contracts;

import by.obs.portal.common.Constants;
import by.obs.portal.persistence.model.base.AbstractNamedEntity;
import by.obs.portal.validation.view.ErrorSequence;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "report_generator")
public class ReportGenerator extends AbstractNamedEntity {

    @NotEmpty(message = "{reportGenerator.contractType.notEmpty}", groups = ErrorSequence.First.class)
    @Size(min = 1, max = 10, message = "{reportGenerator.contractType.size}", groups = ErrorSequence.Second.class)
    @Column(name = "c_type")
    String contractType;

    @NotNull(message = "{reportGenerator.contractNumber.notNull}", groups = ErrorSequence.First.class)
    @Min(value = 1, message = "{reportGenerator.contractNumber.min}", groups = ErrorSequence.Second.class)
    @Column(name = "c_number")
    Integer contractNumber;

    @NotNull(message = "{reportGenerator.contractDate.notNull}")
    @DateTimeFormat(pattern = Constants.DATE_PATTERN)
    @Column(name = "c_date")
    LocalDate contractDate;

    @NotNull(message = "{reportGenerator.unp.notNull}", groups = ErrorSequence.First.class)
    @Range(min = 100000000, max = 999999999, message = "{reportGenerator.unp.range}", groups = ErrorSequence.Second.class)
    @Column(name = "unp")
    Integer unp;

    @NotNull(message = "{reportGenerator.usersAmount.notNull}", groups = ErrorSequence.First.class)
    @Min(value = 1, message = "{reportGenerator.usersAmount.min}", groups = ErrorSequence.Second.class)
    @Column(name = "users_amount")
    Integer usersAmount;
}