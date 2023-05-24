package aber.dcs.uk.shootingCompetitionsBackend.dtos.admin;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.UpdateUserDetailsDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.models.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class AdminRoleUserDto extends UpdateUserDetailsDto {

    @NotNull(message = "Enabled null is not allowed")
    private Boolean enabled;

    @NotBlank(message = "Role blank is not allowed")
    private String role;

    public AdminRoleUserDto() {
    }

    public AdminRoleUserDto(String firstname, String lastname, String email,
            Boolean enabled, Role role) {
        super(firstname, lastname, email);
        this.enabled = enabled;
        this.role = role.name();
    }

    public AdminRoleUserDto(UserEntity user) {
        super(user.getFirstname(), user.getLastname(), user.getEmail());
        this.enabled = user.isEnabled();
        this.role = user.getRole().name();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AdminRoleUserDto that = (AdminRoleUserDto) o;
        return enabled.equals(that.enabled) && role.equals(that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enabled, role);
    }
}
