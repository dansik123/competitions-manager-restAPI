package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.UpdateUserDetailsDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;

import java.util.Objects;

public class UserDetailsResponse extends UpdateUserDetailsDto {
    private Long id;

    public UserDetailsResponse() {
        super();
    }

    public UserDetailsResponse(Long id, String firstname, String lastname, String email) {
        super(firstname, lastname, email);
        this.id = id;
    }

    public UserDetailsResponse(UserEntity updatedUser) {
        super(updatedUser.getFirstname(), updatedUser.getLastname(), updatedUser.getEmail());
        this.id = updatedUser.getId();
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
        UserDetailsResponse that = (UserDetailsResponse) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
