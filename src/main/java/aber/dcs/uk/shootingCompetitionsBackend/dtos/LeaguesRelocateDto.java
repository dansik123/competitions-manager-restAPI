package aber.dcs.uk.shootingCompetitionsBackend.dtos;

import java.util.List;
import java.util.Objects;

public class LeaguesRelocateDto {
    String leaguesGroupPrefix;
    List<Long> groupAllLeaguesIds;

    public LeaguesRelocateDto() {
    }

    public LeaguesRelocateDto(
            String leaguesGroupPrefix, List<Long> groupAllLeaguesIds) {
        this.leaguesGroupPrefix = leaguesGroupPrefix;
        this.groupAllLeaguesIds = groupAllLeaguesIds;
    }

    public String getLeaguesGroupPrefix() {
        return leaguesGroupPrefix;
    }

    public void setLeaguesGroupPrefix(String leaguesGroupPrefix) {
        this.leaguesGroupPrefix = leaguesGroupPrefix;
    }

    public List<Long> getGroupAllLeaguesIds() {
        return groupAllLeaguesIds;
    }

    public void setGroupAllLeaguesIds(List<Long> groupAllLeaguesIds) {
        this.groupAllLeaguesIds = groupAllLeaguesIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaguesRelocateDto that = (LeaguesRelocateDto) o;
        return Objects.equals(leaguesGroupPrefix, that.leaguesGroupPrefix) &&
                Objects.equals(groupAllLeaguesIds, that.groupAllLeaguesIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaguesGroupPrefix, groupAllLeaguesIds);
    }
}
