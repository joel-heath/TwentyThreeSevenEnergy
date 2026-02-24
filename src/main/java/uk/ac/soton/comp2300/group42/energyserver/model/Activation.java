package uk.ac.soton.comp2300.group42.energyserver.model;

import jakarta.persistence.*;
import uk.ac.soton.comp2300.group42.activation.ActivationType;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Activation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Appliance appliance;

    @Column(nullable = false)
    private ActivationType type;

    @Column(nullable = false)
    private LocalTime activationTime;

    private LocalDate activationDate;
    private Boolean recursMonday;
    private Boolean recursTuesday;
    private Boolean recursWednesday;
    private Boolean recursThursday;
    private Boolean recursFriday;
    private Boolean recursSaturday;
    private Boolean recursSunday;

    public Long getId() { return id; }

    public Appliance getAppliance() { return appliance; }
    public void setAppliance(Appliance appliance) { this.appliance = appliance; }

    public ActivationType getType() { return type; }
    public void setType(ActivationType type) { this.type = type; }

    public LocalTime getActivationTime() { return activationTime; }
    public void setActivationTime(LocalTime activationTime) { this.activationTime = activationTime; }

    public LocalDate getActivationDate() { return activationDate; }
    public void setActivationDate(LocalDate activationDate) { this.activationDate = activationDate; }

    public Boolean getRecursMonday() { return recursMonday; }
    public void setRecursMonday(Boolean recursMonday) { this.recursMonday = recursMonday; }

    public Boolean getRecursTuesday() { return recursTuesday; }
    public void setRecursTuesday(Boolean recursTuesday) { this.recursTuesday = recursTuesday; }

    public Boolean getRecursWednesday() { return recursWednesday; }
    public void setRecursWednesday(Boolean recursWednesday) { this.recursWednesday = recursWednesday; }

    public Boolean getRecursThursday() { return recursThursday; }
    public void setRecursThursday(Boolean recursThursday) { this.recursThursday = recursThursday; }

    public Boolean getRecursFriday() { return recursFriday; }
    public void setRecursFriday(Boolean recursFriday) { this.recursFriday = recursFriday; }

    public Boolean getRecursSaturday() { return recursSaturday; }
    public void setRecursSaturday(Boolean recursSaturday) { this.recursSaturday = recursSaturday; }

    public Boolean getRecursSunday() { return recursSunday; }
    public void setRecursSunday(Boolean recursSunday) { this.recursSunday = recursSunday; }
}
