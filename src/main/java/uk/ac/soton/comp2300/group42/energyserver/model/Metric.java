package uk.ac.soton.comp2300.group42.energyserver.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;

import java.time.LocalDateTime;

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
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private EnergyCategory energyCategory;

    @Column(nullable = false)
    private Double energyUsed;

    @Column(nullable = false)
    private Double energyPrice;

    public Long getId() { return id; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public Double getEnergyUsed() { return energyUsed; }
    public void setEnergyUsed(Double energyUsed) { this.energyUsed = energyUsed; }

    public Double getEnergyPrice() { return energyPrice; }
    public void setEnergyPrice(Double energyPrice) { this.energyPrice = energyPrice; }

    public EnergyCategory getEnergyCategory() { return energyCategory; }
    public void setEnergyCategory(EnergyCategory energyCategory) { this.energyCategory = energyCategory; }
}
