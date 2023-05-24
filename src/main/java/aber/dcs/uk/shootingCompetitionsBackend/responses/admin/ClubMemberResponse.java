package aber.dcs.uk.shootingCompetitionsBackend.responses.admin;

import aber.dcs.uk.shootingCompetitionsBackend.dao.UserMemberDao;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubMemberEntity;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserMemberResponse;

import java.util.Objects;

public class ClubMemberResponse {
    private Long clubId;
    private String clubName;
    private UserMemberResponse member;

    public ClubMemberResponse() {
    }

    public ClubMemberResponse(ClubMemberEntity savedClubMember) {
        this.clubId = savedClubMember.getClub().getId();
        this.clubName = savedClubMember.getClub().getClubName();
        this.member = new UserMemberResponse(savedClubMember.getMember());
    }

    public ClubMemberResponse(Long clubId, String clubName, UserMemberDao member) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.member = new UserMemberResponse(member);
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public UserMemberResponse getMember() {
        return member;
    }

    public void setMember(UserMemberResponse member) {
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubMemberResponse that = (ClubMemberResponse) o;
        return clubId.equals(that.clubId) && clubName.equals(that.clubName) && member.equals(that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clubId, clubName, member);
    }
}
