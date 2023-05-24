package aber.dcs.uk.shootingCompetitionsBackend.responses.admin;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.AdminRoleUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.models.Role;

import java.util.Objects;

public class AdminRoleUserResponse extends AdminRoleUserDto {
    private Long id;

    public AdminRoleUserResponse() {
        super();
    }

    public AdminRoleUserResponse(Long id, String firstname, String lastname, String email,
                      Boolean enabled, Role role) {
        super(firstname,lastname, email, enabled, role);
        this.id = id;
    }

    public AdminRoleUserResponse(UserEntity user) {
        super(user.getFirstname(), user.getLastname(),
                user.getEmail(), user.isEnabled(), user.getRole());
        this.id = user.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AdminRoleUserResponse response = (AdminRoleUserResponse) o;
        return id.equals(response.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
