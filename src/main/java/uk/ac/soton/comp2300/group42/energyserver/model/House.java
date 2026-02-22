package uk.ac.soton.comp2300.group42.energyserver.model;

import jakarta.persistence.*;

import java.time.ZoneId;

@Entity
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private ZoneId timezone;

    public Long getId() { return id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public ZoneId getTimezone() { return timezone; }
    public void setTimezone(ZoneId timezone) { this.timezone = timezone; }
}