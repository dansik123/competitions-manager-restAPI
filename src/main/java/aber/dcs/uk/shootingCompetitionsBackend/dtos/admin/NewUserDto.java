package aber.dcs.uk.shootingCompetitionsBackend.dtos.admin;

import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.models.Role;

import java.util.Objects;

public class NewUserDto extends AdminRoleUserDto{
    private String password;

    public NewUserDto() {
        super();
    }

    public NewUserDto(String firstname, String lastname, String email,
                      Boolean enabled, Role role, String password) {
        super(firstname, lastname, email, enabled, role);
        this.password = password;
    }

    public NewUserDto(UserEntity user) {
        super(user);
        this.password = user.getPassword();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserEntity toUserEntity(){
        UserEntity user = new UserEntity();
        user.setId(null);
        user.setFirstname(this.getFirstname());
        user.setLastname(this.getLastname());
        user.setEmail(this.getEmail());
        user.setPassword(this.getPassword());
        user.setEnabled(this.getEnabled());
        user.setRole(Role.valueOf(this.getRole()));
        return user;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NewUserDto that = (NewUserDto) o;
        return password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), password);
    }
}
