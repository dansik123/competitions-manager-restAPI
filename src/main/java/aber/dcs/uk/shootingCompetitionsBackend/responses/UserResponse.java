package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;

public class UserResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String role;

    public UserResponse() {
    }

    public UserResponse(Long id, String firstName, String lastName, String email, String role) {
        this.id = id;
        this.firstname = firstName;
        this.lastname = lastName;
        this.email = email;
        this.role = role;
    }

    public UserResponse(UserEntity user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.role = user.getRole().name();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
