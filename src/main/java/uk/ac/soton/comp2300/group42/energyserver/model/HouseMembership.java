package uk.ac.soton.comp2300.group42.energyserver.model;

import jakarta.persistence.*;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.model.converter.RoleConverter;

@Entity
@Table(name = "user_house_link")
public class HouseMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Convert(converter = RoleConverter.class)
    private Role role;

    private String houseName;

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getHouseName() { return houseName; }
    public void setHouseName(String houseName) { this.houseName = houseName; }
}