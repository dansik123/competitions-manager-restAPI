package aber.dcs.uk.shootingCompetitionsBackend.dtos;

import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterUserDto {
    @NotBlank(message = "Firstname can't be empty")
    private String firstname;
    @NotBlank(message = "Lastname can't be empty")
    private String lastname;
    @NotBlank(message = "Email can't be empty")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "Password can't be empty")
    private String password;

    public RegisterUserDto(){

    }
    public RegisterUserDto(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserEntity toUserEntity(){
        return
            new UserEntity(null,
                this.firstname,
                this.lastname,
                this.email,
                this.password,
                true, //Change later to false user must be enabled by ADMIN
                Role.USER
            );
    }
}
