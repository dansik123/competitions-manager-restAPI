package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.dao.UserMemberDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;

import java.util.Objects;

public class UserMemberResponse implements UserMemberDao {
    private Long userId;
    private String firstname;
    private String lastname;

    public UserMemberResponse() {
    }

    public UserMemberResponse(Long id, String firstname, String lastname) {
        this.userId = id;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public UserMemberResponse(UserEntity userEntity) {
        this.userId = userEntity.getId();
        this.firstname = userEntity.getFirstname();
        this.lastname = userEntity.getLastname();
    }

    public UserMemberResponse(UserMemberDao userMemberDao){
        this.userId = userMemberDao.getUserId();
        this.firstname = userMemberDao.getFirstname();
        this.lastname = userMemberDao.getLastname();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserMemberResponse that = (UserMemberResponse) o;
        return userId.equals(that.userId) && firstname.equals(that.firstname) && lastname.equals(that.lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstname, lastname);
    }
}
