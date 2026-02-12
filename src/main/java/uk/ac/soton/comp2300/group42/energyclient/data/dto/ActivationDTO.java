package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ActivationDTO {
    private final Long id;
    private Long applianceId;
    private LocalTime activationTime;
    private LocalDate activationDate;
    private boolean recursMonday;
    private boolean recursTuesday;
    private boolean recursWednesday;
    private boolean recursThursday;
    private boolean recursFriday;
    private boolean recursSaturday;
    private boolean recursSunday;

    public ActivationDTO(Long applianceId, LocalTime activationTime,
                         boolean recursMonday,
                         boolean recursTuesday,
                         boolean recursWednesday,
                         boolean recursThursday,
                         boolean recursFriday,
                         boolean recursSaturday,
                         boolean recursSunday) {
        this(null, applianceId, activationTime, null, recursMonday, recursTuesday, recursWednesday, recursThursday, recursFriday, recursSaturday, recursSunday);
    }

    public ActivationDTO(Long applianceId, LocalTime activationTime, LocalDate activationDate) {
        this(null, applianceId, activationTime, activationDate, false, false, false, false, false, false, false);
    }

    public ActivationDTO(Long id, Long applianceId, LocalTime activationTime, LocalDate activationDate,
                         boolean recursMonday,
                         boolean recursTuesday,
                         boolean recursWednesday,
                         boolean recursThursday,
                         boolean recursFriday,
                         boolean recursSaturday,
                         boolean recursSunday) {
        this.id = id;
        this.applianceId = applianceId;
        this.activationTime = activationTime;
        this.activationDate = activationDate;
        this.recursMonday = recursMonday;
        this.recursTuesday = recursTuesday;
        this.recursWednesday = recursWednesday;
        this.recursThursday = recursThursday;
        this.recursFriday = recursFriday;
        this.recursSaturday = recursSaturday;
        this.recursSunday = recursSunday;
    }

    public Long getId() { return id; }

    public Long getApplianceId() { return applianceId; }
    public void setApplianceId(Long applianceId) { this.applianceId = applianceId; }

    public LocalTime getActivationTime() { return activationTime; }
    public void setActivationTime(LocalTime activationTime) { this.activationTime = activationTime; }

    public LocalDate getActivationDate() { return activationDate; }
    public void setActivationDate(LocalDate activationDate) { this.activationDate = activationDate; }

    public Boolean isRecursMonday() { return recursMonday; }
    public void setRecursMonday(Boolean recursMonday) { this.recursMonday = recursMonday; }

    public Boolean isRecursTuesday() { return recursTuesday; }
    public void setRecursTuesday(Boolean recursTuesday) { this.recursTuesday = recursTuesday; }

    public Boolean isRecursWednesday() { return recursWednesday; }
    public void setRecursWednesday(Boolean recursWednesday) { this.recursWednesday = recursWednesday; }

    public Boolean isRecursThursday() { return recursThursday; }
    public void setRecursThursday(Boolean recursThursday) { this.recursThursday = recursThursday; }

    public Boolean isRecursFriday() { return recursFriday; }
    public void setRecursFriday(Boolean recursFriday) { this.recursFriday = recursFriday; }

    public Boolean isRecursSaturday() { return recursSaturday; }
    public void setRecursSaturday(Boolean recursSaturday) { this.recursSaturday = recursSaturday; }

    public Boolean isRecursSunday() { return recursSunday; }
    public void setRecursSunday(Boolean recursSunday) { this.recursSunday = recursSunday; }
}
