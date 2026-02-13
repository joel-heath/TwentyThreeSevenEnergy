package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import uk.ac.soton.comp2300.group42.energyclient.data.api.Role;

public class HousemateDTO extends UserDTO {

    private final Long houseId;
    private Role role;

    public HousemateDTO(Long id, String forename, String surname, String email, Long houseId, Role role) {
        super(id, forename, surname, email);
        this.houseId = houseId;
        this.role = role;
    }

    public Long getHouseId() { return houseId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
