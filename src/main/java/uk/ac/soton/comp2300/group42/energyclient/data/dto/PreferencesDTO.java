package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Theme;

public class PreferencesDTO {
    
    private boolean largeFont;
    private ColorVision vision;
    private Theme theme;
    private Mode mode;
    private boolean shareLocation;
    private double energyGoal;
    private Long activeHouseId;

    public boolean getLargeFont() { return largeFont; }
    public void setLargeFont(boolean largeFont) { this.largeFont = largeFont; }

    public Theme getTheme() { return theme; }
    public void setTheme(Theme theme) { this.theme = theme; }

    public ColorVision getVision() { return vision; }
    public void setVision(ColorVision vision) { this.vision = vision; }

    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }

    public boolean getShareLocation() { return shareLocation; }
    public void setShareLocation(boolean shareLocation) { this.shareLocation = shareLocation; }

    public double getEnergyGoal() { return energyGoal; }
    public void setEnergyGoal(double energyGoal) { this.energyGoal = energyGoal; }

    public Long getActiveHouseId() { return activeHouseId; }
    public void setActiveHouseId(Long activeHouseId) { this.activeHouseId = activeHouseId; }

    public PreferencesDTO(boolean largeFont, ColorVision vision, Theme theme, Mode mode, boolean shareLocation, double energyGoal, Long activeHouseId) {
        this.largeFont = largeFont;
        this.vision = vision;
        this.theme = theme;
        this.mode = mode;
        this.shareLocation = shareLocation;
        this.energyGoal = energyGoal;
        this.activeHouseId = activeHouseId;
    }

    public PreferencesDTO() {
        this(false, ColorVision.TYPICAL, Theme.LIGHT, Mode.SIMPLE, false, 1, -1L);
    }
}
