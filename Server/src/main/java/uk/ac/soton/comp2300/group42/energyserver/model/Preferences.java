package uk.ac.soton.comp2300.group42.energyserver.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;

@Entity
public class Preferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    // @OnDelete(action = OnDeleteAction.SET_NULL) // On Delete is managed by HouseService, it should never be null.
    private House activeHouse;

    @Column(nullable = false)
    private Boolean largeFont;

    @Column(nullable = false)
    private ColorVision colorVision;

    @Column(nullable = false)
    private Theme theme;

    @Column(nullable = false)
    private Mode mode;

    @Column(nullable = false)
    private Boolean shareLocation;

    @Column(nullable = false)
    private Double energyGoal;

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public House getActiveHouse() { return activeHouse; }
    public void setActiveHouse(House activeHouse) { this.activeHouse = activeHouse; }

    public Boolean getLargeFont() { return largeFont; }
    public void setLargeFont(Boolean largeFont) { this.largeFont = largeFont; }

    public ColorVision getColorVision() { return colorVision; }
    public void setColorVision(ColorVision colorVision) { this.colorVision = colorVision; }

    public Theme getTheme() { return theme; }
    public void setTheme(Theme theme) { this.theme = theme; }

    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }

    public Boolean getShareLocation() { return shareLocation; }
    public void setShareLocation(Boolean shareLocation) { this.shareLocation = shareLocation; }

    public Double getEnergyGoal() { return energyGoal; }
    public void setEnergyGoal(Double energyGoal) { this.energyGoal = energyGoal; }
}
