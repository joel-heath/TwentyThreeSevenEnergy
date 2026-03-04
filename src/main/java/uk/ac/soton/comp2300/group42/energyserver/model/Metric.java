package uk.ac.soton.comp2300.group42.energyserver.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private House house;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double energyUsed;

    public Long getId() { return id; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getEnergyUsed() { return energyUsed; }
    public void setEnergyUsed(Double energyUsed) { this.energyUsed = energyUsed; }
}
