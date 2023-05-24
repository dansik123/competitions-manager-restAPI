package aber.dcs.uk.shootingCompetitionsBackend.dtos;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SaveLeagueDto {
    private Integer roundsToPlay;
    private Integer leagueMaxCompetitors;
    private String leagueGunType;
    private Map<String, List<Long>> leaguesGroups;

    public SaveLeagueDto() {
    }

    public SaveLeagueDto(Integer roundsToPlay, Integer leagueMaxCompetitors,
                         String leagueGunType, Map<String, List<Long>> leaguesGroups) {
        this.roundsToPlay = roundsToPlay;
        this.leagueMaxCompetitors = leagueMaxCompetitors;
        this.leagueGunType = leagueGunType;
        this.leaguesGroups = leaguesGroups;
    }

    public Integer getRoundsToPlay() {
        return roundsToPlay;
    }

    public void setRoundsToPlay(Integer roundsToPlay) {
        this.roundsToPlay = roundsToPlay;
    }

    public Integer getLeagueMaxCompetitors() {
        return leagueMaxCompetitors;
    }

    public void setLeagueMaxCompetitors(Integer leagueMaxCompetitors) {
        this.leagueMaxCompetitors = leagueMaxCompetitors;
    }

    public String getLeagueGunType() {
        return leagueGunType;
    }

    public void setLeagueGunType(String leagueGunType) {
        this.leagueGunType = leagueGunType;
    }

    public Map<String, List<Long>> getLeaguesGroups() {
        return leaguesGroups;
    }

    public void setLeaguesGroups(Map<String, List<Long>> leaguesGroups) {
        this.leaguesGroups = leaguesGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveLeagueDto that = (SaveLeagueDto) o;
        return Objects.equals(roundsToPlay, that.roundsToPlay) &&
                Objects.equals(leagueMaxCompetitors, that.leagueMaxCompetitors) &&
                Objects.equals(leagueGunType, that.leagueGunType) &&
                Objects.equals(leaguesGroups, that.leaguesGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundsToPlay, leagueMaxCompetitors, leagueGunType, leaguesGroups);
    }
}
