package aber.dcs.uk.shootingCompetitionsBackend.dtos;

import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class UpdateUserDetailsDto {

    @NotBlank(message = "Firstname blank is not allowed")
    private String firstname;

    @NotBlank(message = "Lastname blank is not allowed")
    private String lastname;

    @NotBlank(message = "Email blank is not allowed")
    @Email(message = "Email must be valid")
    private String email;

    public UpdateUserDetailsDto() {
    }

    public UpdateUserDetailsDto(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public UpdateUserDetailsDto(UserEntity databaseUserEntity) {
        this.firstname = databaseUserEntity.getFirstname();
        this.lastname = databaseUserEntity.getLastname();
        this.email = databaseUserEntity.getEmail();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateUserDetailsDto that = (UpdateUserDetailsDto) o;
        return firstname.equals(that.firstname) && lastname.equals(that.lastname) && email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, email);
    }
}
