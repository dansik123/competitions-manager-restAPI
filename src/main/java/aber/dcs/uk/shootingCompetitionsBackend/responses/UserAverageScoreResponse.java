package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ScoreDetailsDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.UserMemberDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.AverageShootingScoreEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserAverageScoreResponse{
    private Long userId;
    private String userFirstname;
    private String userSurname;
    private List<ScoreDetailsResponse> scoresDetails;

    public UserAverageScoreResponse() {
        scoresDetails = new ArrayList<>();
    }

    public UserAverageScoreResponse(Long userId, String userFirstname, String userSurname,
                                    List<ScoreDetailsResponse> scoreDetails) {
        this.userId = userId;
        this.userFirstname = userFirstname;
        this.userSurname = userSurname;
        this.scoresDetails = scoreDetails;
    }

    public UserAverageScoreResponse(AverageShootingScoreEntity averageShootingScore){
        UserEntity user = averageShootingScore.getUser();
        this.userId = user.getId();
        this.userFirstname = user.getFirstname();
        this.userSurname = user.getLastname();
        this.scoresDetails = new ArrayList<>(1);
        scoresDetails.add(new ScoreDetailsResponse(
                averageShootingScore.getGunType().name(),
                averageShootingScore.getAverageScore()));
    }

    public UserAverageScoreResponse(UserMemberDao userDetails, List<ScoreDetailsResponse> scoreDetails) {
        this.userId = userDetails.getUserId();
        this.userFirstname = userDetails.getFirstname();
        this.userSurname = userDetails.getLastname();
        this.scoresDetails = scoreDetails;
    }

    public UserAverageScoreResponse(UserMemberDao userDetails, ScoreDetailsDao averageShootingScore) {
        this.userId = userDetails.getUserId();
        this.userFirstname = userDetails.getFirstname();
        this.userSurname = userDetails.getLastname();
        this.scoresDetails = new ArrayList<>(List.of(
                new ScoreDetailsResponse(averageShootingScore)
        ));
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFirstname() {
        return userFirstname;
    }

    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public List<ScoreDetailsResponse> getScoresDetails() {
        return scoresDetails;
    }

    public void setScoresDetails(List<ScoreDetailsResponse> scoresDetails) {
        this.scoresDetails = scoresDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAverageScoreResponse that = (UserAverageScoreResponse) o;
        return userId.equals(that.userId) && userFirstname.equals(that.userFirstname) &&
                userSurname.equals(that.userSurname) && scoresDetails.equals(that.scoresDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userFirstname, userSurname, scoresDetails);
    }
}
