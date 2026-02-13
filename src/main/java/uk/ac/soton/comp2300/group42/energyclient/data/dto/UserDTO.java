package uk.ac.soton.comp2300.group42.energyclient.data.dto;

public class UserDTO {
    
    private final Long id;
    private String forename;
    private String surname;
    private String email;

    public Long getId() { return id; }

    public String getForename() { return forename; }
    public void setForename(String forename) { this.forename = forename; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserDTO(Long id, String forename, String surname, String email) {
        this.id = id;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
    }
}
