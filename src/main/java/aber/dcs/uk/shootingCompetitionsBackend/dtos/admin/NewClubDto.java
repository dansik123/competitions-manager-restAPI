package aber.dcs.uk.shootingCompetitionsBackend.dtos.admin;

import java.util.Objects;

public class NewClubDto {
    private String newClubName;

    public NewClubDto() {
    }

    public NewClubDto(String newClubName) {
        this.newClubName = newClubName;
    }

    public String getNewClubName() {
        return newClubName;
    }

    public void setNewClubName(String newClubName) {
        this.newClubName = newClubName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewClubDto that = (NewClubDto) o;
        return newClubName.equals(that.newClubName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newClubName);
    }
}
