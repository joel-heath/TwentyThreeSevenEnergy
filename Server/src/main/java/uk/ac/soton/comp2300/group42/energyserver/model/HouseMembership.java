package uk.ac.soton.comp2300.group42.energyserver.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.model.converter.RoleConverter;

@Entity
public class HouseMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private House house;

    @Convert(converter = RoleConverter.class)
    private Role role;

    @Column(nullable = false)
    private String houseNickname;

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getHouseNickname() { return houseNickname; }
    public void setHouseNickname(String houseName) { this.houseNickname = houseName; }
}