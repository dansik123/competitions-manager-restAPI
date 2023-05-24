package aber.dcs.uk.shootingCompetitionsBackend.responses;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ShootingSlotDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingSlotEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;
import java.util.Objects;

public class MatchResponse {
    private Long matchId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate matchDate;
    private UserMemberResponse competitor1;
    private UserMemberResponse competitor2;
    private String slotMatchResult;
    private String competitor1ScoreCardLink;
    private String competitor2ScoreCardLink;

    public MatchResponse() {
    }

    public MatchResponse(Long matchId,
                         LocalDate matchDate,
                         UserMemberResponse competitor1, UserMemberResponse competitor2,
                         String slotMatchResult,
                         String competitor1ScoreCardLink, String competitor2ScoreCardLink) {
        this.matchId = matchId;
        this.matchDate = matchDate;
        this.competitor1 = competitor1;
        this.competitor2 = competitor2;
        this.slotMatchResult = slotMatchResult;
        this.competitor1ScoreCardLink = competitor1ScoreCardLink;
        this.competitor2ScoreCardLink = competitor2ScoreCardLink;
    }

    public MatchResponse(ShootingSlotEntity shootingSlotEntity) {
        this.matchId = shootingSlotEntity.getId();
        this.matchDate = (shootingSlotEntity.getSlotDate() == null)? null:
                LocalDate.parse(shootingSlotEntity.getSlotDate().toString());
        this.competitor1 = new UserMemberResponse(shootingSlotEntity.getCompetitor1());
        this.competitor2 = new UserMemberResponse(shootingSlotEntity.getCompetitor2());
        this.slotMatchResult = (shootingSlotEntity.getHasScoreResult())?
                String.format("%d-%d", shootingSlotEntity.getCompetitor1Score(),
                        shootingSlotEntity.getCompetitor2Score()) : "unmarked";
        this.competitor1ScoreCardLink = shootingSlotEntity.getCompetitor1ScoreCardLink();
        this.competitor2ScoreCardLink = shootingSlotEntity.getCompetitor2ScoreCardLink();

    }

    public MatchResponse(ShootingSlotDao shootingSlotDao) {
        this.matchId = shootingSlotDao.getId();
        this.matchDate =
                (shootingSlotDao.getSlotDate()==null)? null: shootingSlotDao.getSlotDate().toLocalDate();
        this.competitor1 = new UserMemberResponse(shootingSlotDao.getCompetitor1());
        this.competitor2 = new UserMemberResponse(shootingSlotDao.getCompetitor2());
        this.slotMatchResult = shootingSlotDao.getSlotMatchResult();
        this.competitor1ScoreCardLink = shootingSlotDao.getCompetitor1ScoreCardLink();
        this.competitor2ScoreCardLink = shootingSlotDao.getCompetitor2ScoreCardLink();
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    public UserMemberResponse getCompetitor1() {
        return competitor1;
    }

    public void setCompetitor1(UserMemberResponse competitor1) {
        this.competitor1 = competitor1;
    }

    public UserMemberResponse getCompetitor2() {
        return competitor2;
    }

    public void setCompetitor2(UserMemberResponse competitor2) {
        this.competitor2 = competitor2;
    }

    public String getSlotMatchResult() {
        return slotMatchResult;
    }

    public void setSlotMatchResult(String slotMatchResult) {
        this.slotMatchResult = slotMatchResult;
    }

    public String getCompetitor1ScoreCardLink() {
        return competitor1ScoreCardLink;
    }

    public void setCompetitor1ScoreCardLink(String competitor1ScoreCardLink) {
        this.competitor1ScoreCardLink = competitor1ScoreCardLink;
    }

    public String getCompetitor2ScoreCardLink() {
        return competitor2ScoreCardLink;
    }

    public void setCompetitor2ScoreCardLink(String competitor2ScoreCardLink) {
        this.competitor2ScoreCardLink = competitor2ScoreCardLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchResponse that = (MatchResponse) o;
        return Objects.equals(matchId, that.matchId) && Objects.equals(matchDate, that.matchDate) &&
                Objects.equals(competitor1, that.competitor1) &&
                Objects.equals(competitor2, that.competitor2) &&
                Objects.equals(slotMatchResult, that.slotMatchResult) &&
                Objects.equals(competitor1ScoreCardLink, that.competitor1ScoreCardLink) &&
                Objects.equals(competitor2ScoreCardLink, that.competitor2ScoreCardLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, matchDate, competitor1, competitor2,
                slotMatchResult, competitor1ScoreCardLink, competitor2ScoreCardLink);
    }
}
