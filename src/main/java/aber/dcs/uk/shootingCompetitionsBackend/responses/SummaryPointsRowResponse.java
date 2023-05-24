package aber.dcs.uk.shootingCompetitionsBackend.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SummaryPointsRowResponse<T> implements Comparable<SummaryPointsRowResponse<T>>{
    private UserMemberResponse pointsOwner;
    private List<T> individualPoints;
    private Integer totalLeaguePoints;

    public SummaryPointsRowResponse() {
    }

    public SummaryPointsRowResponse(UserMemberResponse pointsOwner) {
        this.pointsOwner = pointsOwner;
        individualPoints = new ArrayList<>();
        totalLeaguePoints = 0;
    }

    public SummaryPointsRowResponse(
            UserMemberResponse pointsOwner,
            List<T> individualPoints,
            Integer totalLeaguePoints) {
        this.pointsOwner = pointsOwner;
        this.individualPoints = individualPoints;
        this.totalLeaguePoints = totalLeaguePoints;
    }

    public UserMemberResponse getPointsOwner() {
        return pointsOwner;
    }

    public void setPointsOwner(UserMemberResponse pointsOwner) {
        this.pointsOwner = pointsOwner;
    }

    public List<T> getIndividualPoints() {
        return individualPoints;
    }

    public void setIndividualPoints(List<T> individualPoints) {
        this.individualPoints = individualPoints;
    }

    public Integer getTotalLeaguePoints() {
        return totalLeaguePoints;
    }

    public void setTotalLeaguePoints(Integer totalLeaguePoints) {
        this.totalLeaguePoints = totalLeaguePoints;
    }

    public void addToTotalPoints(Integer newPoints){
        if(newPoints != null) {
            totalLeaguePoints += newPoints;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummaryPointsRowResponse<?> that = (SummaryPointsRowResponse<?>) o;
        return Objects.equals(pointsOwner, that.pointsOwner) &&
                Objects.equals(individualPoints, that.individualPoints) &&
                Objects.equals(totalLeaguePoints, that.totalLeaguePoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointsOwner, individualPoints, totalLeaguePoints);
    }

    @Override
    public int compareTo(SummaryPointsRowResponse o) {
        return this.totalLeaguePoints.compareTo(o.totalLeaguePoints);
    }
}
