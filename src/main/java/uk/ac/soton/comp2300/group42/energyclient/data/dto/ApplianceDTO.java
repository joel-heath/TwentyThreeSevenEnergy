package uk.ac.soton.comp2300.group42.energyclient.data.dto;

public class ApplianceDTO {

    private final Long id;
    private String name;
    // MoSCoW Could Have: An icon, customisable by the user from a preset list of icons, that is displayed when scheduling.
    //        Could Have: A Google Home ID, for Google Home integration

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ApplianceDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
