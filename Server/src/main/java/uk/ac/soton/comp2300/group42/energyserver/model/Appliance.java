package uk.ac.soton.comp2300.group42.energyserver.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Appliance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private House house;

    @Column(nullable = false)
    private String name;

    public Long getId() { return id; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
