package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ShootingSlotDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingSlotEntity;

import java.time.LocalDate;
import java.util.Objects;

public class SingleMatchResponse extends MatchResponse {
    private Integer roundNo;

    public SingleMatchResponse() {
    }

    public SingleMatchResponse(Long matchId, LocalDate matchDate, UserMemberResponse competitor1,
                               UserMemberResponse competitor2, String slotMatchResult,
                               String competitor1ScoreCardLink, String competitor2ScoreCardLink,
                               Integer roundNo) {
        super(matchId, matchDate, competitor1, competitor2,
                slotMatchResult, competitor1ScoreCardLink, competitor2ScoreCardLink);
        this.roundNo = roundNo;
    }

    public SingleMatchResponse(ShootingSlotDao shootingSlotDao) {
        super(shootingSlotDao);
        this.roundNo = shootingSlotDao.getRoundNumber();
    }

    public SingleMatchResponse(ShootingSlotEntity shootingSlotEntity) {
        super(shootingSlotEntity);
        this.roundNo = shootingSlotEntity.getMatchRoundNumber();
    }

    public Integer getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(Integer roundNo) {
        this.roundNo = roundNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SingleMatchResponse that = (SingleMatchResponse) o;
        return roundNo.equals(that.roundNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roundNo);
    }
}
