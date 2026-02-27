package uk.ac.soton.comp2300.group42.energyserver.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
public class Metrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private House house;

    @Column(nullable = false)
    private List<Double> metrics;

    public Long getId() { return id; }

    public List<Double> getMetrics() { return metrics; }
    public void setMetrics(List<Double> metrics) { this.metrics = metrics; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }
}
