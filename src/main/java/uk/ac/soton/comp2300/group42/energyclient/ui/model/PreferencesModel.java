package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Theme;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelUtils.updateIfChanged;

public class PreferencesModel {
    private final PreferencesDTO dto;
    private final BooleanProperty largeFont;
    private final ObjectProperty<ColorVision> vision;
    private final ObjectProperty<Theme> theme;
    private final ObjectProperty<Mode> mode;
    private final BooleanProperty shareLocation;
    private final DoubleProperty energyGoal;

    public PreferencesModel(PreferencesDTO dto) {
        this.dto = dto;
        this.largeFont = new SimpleBooleanProperty(dto.getLargeFont());
        this.vision = new SimpleObjectProperty<>(dto.getVision());
        this.theme = new SimpleObjectProperty<>(dto.getTheme());
        this.mode = new SimpleObjectProperty<>(dto.getMode());
        this.shareLocation = new SimpleBooleanProperty(dto.getShareLocation());
        this.energyGoal = new SimpleDoubleProperty(dto.getEnergyGoal());
    }

    public PreferencesDTO commit() {
        dto.setLargeFont(largeFont.get());
        dto.setVision(vision.get());
        dto.setTheme(theme.get());
        dto.setMode(mode.get());
        dto.setShareLocation(shareLocation.get());
        dto.setEnergyGoal(energyGoal.get());
        return dto;
    }

    public void updateFrom(PreferencesDTO dto) {
        updateIfChanged(getLargeFont(), dto.getLargeFont(), this::setLargeFont);
        updateIfChanged(getVision(), dto.getVision(), this::setVision);
        updateIfChanged(getTheme(), dto.getTheme(), this::setTheme);
        updateIfChanged(getMode(), dto.getMode(), this::setMode);
        updateIfChanged(getShareLocation(), dto.getShareLocation(), this::setShareLocation);
        updateIfChanged(getEnergyGoal(), dto.getEnergyGoal(), this::setEnergyGoal);
    }

    public boolean getLargeFont() { return largeFont.get(); }
    public void setLargeFont(boolean largeFont) { this.largeFont.set(largeFont); }
    public BooleanProperty largeFontProperty() { return largeFont; }

    public  ColorVision getVision() { return vision.get(); }
    public void setVision(ColorVision vision) { this.vision.set(vision); }
    public ObjectProperty<ColorVision> visionProperty() { return vision; }

    public Theme getTheme() { return theme.get(); }
    public void setTheme(Theme theme) { this.theme.set(theme); }
    public ObjectProperty<Theme> themeProperty() { return theme; }

    public Mode getMode() { return mode.get(); }
    public void setMode(Mode mode) { this.mode.set(mode); }
    public ObjectProperty<Mode> modeProperty() { return mode; }

    public boolean getShareLocation() { return shareLocation.get(); }
    public void setShareLocation(boolean shareLocation) { this.shareLocation.set(shareLocation); }
    public BooleanProperty shareLocationProperty() { return shareLocation; }

    public double getEnergyGoal() { return energyGoal.get(); }
    public void setEnergyGoal(double energyGoal) { this.energyGoal.set(energyGoal); }
    public DoubleProperty energyGoalProperty() { return energyGoal; }
}