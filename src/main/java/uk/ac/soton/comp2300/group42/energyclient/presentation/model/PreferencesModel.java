package uk.ac.soton.comp2300.group42.energyclient.presentation.model;

import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ModelUtils.updateIfChanged;

public class PreferencesModel {

    private final BooleanProperty largeFont;
    private final ObjectProperty<ColorVision> vision;
    private final ObjectProperty<Theme> theme;
    private final ObjectProperty<Mode> mode;
    private final BooleanProperty shareLocation;
    private final DoubleProperty energyGoal;
    private final ObjectProperty<HouseModel> activeHouse;

    public PreferencesModel(Preferences entity, HouseModel activeHouse) {
        this.largeFont = new SimpleBooleanProperty(entity.largeFont());
        this.vision = new SimpleObjectProperty<>(entity.vision());
        this.theme = new SimpleObjectProperty<>(entity.theme());
        this.mode = new SimpleObjectProperty<>(entity.mode());
        this.shareLocation = new SimpleBooleanProperty(entity.shareLocation());
        this.energyGoal = new SimpleDoubleProperty(entity.energyGoal());
        this.activeHouse = new SimpleObjectProperty<>(activeHouse);
    }

    public Preferences commit(Long userId) {
        return new Preferences(
            userId,
            getLargeFont(),
            getVision(),
            getTheme(),
            getMode(),
            getShareLocation(),
            getEnergyGoal(),
            getActiveHouse().getId()
        );
    }

    public void updateFrom(Preferences entity, HouseModel house) {
        updateIfChanged(getLargeFont(), entity.largeFont(), this::setLargeFont);
        updateIfChanged(getVision(), entity.vision(), this::setVision);
        updateIfChanged(getTheme(), entity.theme(), this::setTheme);
        updateIfChanged(getMode(), entity.mode(), this::setMode);
        updateIfChanged(getShareLocation(), entity.shareLocation(), this::setShareLocation);
        updateIfChanged(getEnergyGoal(), entity.energyGoal(), this::setEnergyGoal);
        updateIfChanged(getActiveHouse(), house, this::setActiveHouse);
    }

    public boolean getLargeFont() { return largeFont.get(); }
    public void setLargeFont(boolean largeFont) { this.largeFont.set(largeFont); }
    public BooleanProperty largeFontProperty() { return largeFont; }

    public ColorVision getVision() { return vision.get(); }
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

    public HouseModel getActiveHouse() { return activeHouse.get(); }
    public void setActiveHouse(HouseModel activeHouse) { this.activeHouse.set(activeHouse); }
    public ObjectProperty<HouseModel> activeHouseProperty() { return activeHouse; }
}