package uk.ac.soton.comp2300.group42.energyclient.data.dto;

public class HouseDTO {

    private final Long id;
    private String name;
    private String address;

    public HouseDTO(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
