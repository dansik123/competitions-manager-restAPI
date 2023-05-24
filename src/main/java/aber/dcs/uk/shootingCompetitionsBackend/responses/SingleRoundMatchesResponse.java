package aber.dcs.uk.shootingCompetitionsBackend.responses;

import java.util.List;
import java.util.Objects;

public class SingleRoundMatchesResponse {
    private Integer roundNo;
    private List<UserMemberResponse> roundPauseUsers;
    private List<MatchResponse> matches;

    public SingleRoundMatchesResponse() {
    }

    public SingleRoundMatchesResponse(Integer roundNo,
                                      List<UserMemberResponse> roundPauseUsers,
                                      List<MatchResponse> matches) {
        this.roundNo = roundNo;
        this.roundPauseUsers = roundPauseUsers;
        this.matches = matches;
    }

    public Integer getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(Integer roundNo) {
        this.roundNo = roundNo;
    }

    public List<UserMemberResponse> getRoundPauseUsers() {
        return roundPauseUsers;
    }

    public void setRoundPauseUsers(List<UserMemberResponse> roundPauseUsers) {
        this.roundPauseUsers = roundPauseUsers;
    }

    public List<MatchResponse> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchResponse> matches) {
        this.matches = matches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleRoundMatchesResponse that = (SingleRoundMatchesResponse) o;
        return Objects.equals(roundNo, that.roundNo) &&
                Objects.equals(roundPauseUsers, that.roundPauseUsers) &&
                Objects.equals(matches, that.matches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundNo, roundPauseUsers, matches);
    }
}
