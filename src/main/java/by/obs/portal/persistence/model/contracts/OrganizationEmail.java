package by.obs.portal.persistence.model.contracts;

import by.obs.portal.persistence.model.base.AbstractBaseEntity;
import by.obs.portal.validation.ValidEmail;
import by.obs.portal.validation.view.ErrorSequence;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "rep_gen_email", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "report_generator_email_idx")})
public class OrganizationEmail extends AbstractBaseEntity {

    @NotEmpty(message = "{organizationEmail.email.notEmpty}", groups = ErrorSequence.First.class)
    @ValidEmail(message = "{organizationEmail.email.validEmail}", groups = ErrorSequence.Second.class)
    @Column(name = "email")
    String email;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "rep_gen_id", nullable = false)
    @JsonIgnore
    ReportGenerator reportGenerator;
}