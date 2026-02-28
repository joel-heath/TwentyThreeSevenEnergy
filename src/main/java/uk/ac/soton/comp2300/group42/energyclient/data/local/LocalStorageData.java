package uk.ac.soton.comp2300.group42.energyclient.data.local;

import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.*;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class LocalStorageData {
    public User user;
    public Housemate housemate;
    public Preferences preferences;
    public Map<Long, Appliance> appliances;
    public Map<Long, Activation> activations;
    public Map<Long, House> houses;

    public LocalStorageData() {
        this.user = null;
        this.housemate = null;
        this.preferences = null;
        this.appliances = new HashMap<>();
        this.activations = new HashMap<>();
        this.houses = new HashMap<>();
    }

    public static LocalStorageData createDefault() {
        LocalStorageData data = new LocalStorageData();

        data.user = new User(0L, "Default User", "No email set");
        data.preferences = new Preferences(0L, false,ColorVision.TYPICAL, Theme.LIGHT, Mode.SIMPLE, false, 1.0, 0L);
        data.houses.put(0L, new House(0L, "Default House", "No address set",ZoneId.systemDefault(), Role.OWNER));

        return data;
    }

    public void updateFrom(LocalStorageData newData) {
        this.user = newData.user;
        this.housemate = newData.housemate;
        this.preferences = newData.preferences;
        this.appliances = newData.appliances;
        this.activations = newData.activations;
        this.houses = newData.houses;
    }

    private Long nextId(Map<Long, ?> map) {
        return map.keySet().stream().max(Long::compareTo).orElse(0L) + 1;
    }

    public Long nextApplianceId() {
        return nextId(appliances);
    }

    public Long nextActivationId() {
        return nextId(activations);
    }

    public Long nextHouseId() {
        return nextId(houses);
    }
}
