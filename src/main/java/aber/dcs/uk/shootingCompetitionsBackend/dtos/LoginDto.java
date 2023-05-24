package aber.dcs.uk.shootingCompetitionsBackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDto {
    @NotBlank(message = "Email can't be empty")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "Password can't be empty")
    private String password;

    public LoginDto(){

    }
    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
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
}
