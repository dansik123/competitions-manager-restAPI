package aber.dcs.uk.shootingCompetitionsBackend.dtos.admin;

import java.util.Objects;

public class UpdateLeagueDto {
    private String newLeagueName;

    public UpdateLeagueDto() {
    }

    public UpdateLeagueDto(String newLeagueName) {
        this.newLeagueName = newLeagueName;
    }

    public String getNewLeagueName() {
        return newLeagueName;
    }

    public void setNewLeagueName(String newLeagueName) {
        this.newLeagueName = newLeagueName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateLeagueDto that = (UpdateLeagueDto) o;
        return newLeagueName.equals(that.newLeagueName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newLeagueName);
    }
}
