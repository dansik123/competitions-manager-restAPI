package aber.dcs.uk.shootingCompetitionsBackend.dtos.admin;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class AdminClubMemberDto {
    @NotNull(message = "User id can't be null or empty")
    private Long newMemberId;

    public AdminClubMemberDto() {
    }

    public AdminClubMemberDto(Long newMemberId) {
        this.newMemberId = newMemberId;
    }

    public Long getNewMemberId() {
        return newMemberId;
    }

    public void setNewMemberId(Long newMemberId) {
        this.newMemberId = newMemberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminClubMemberDto that = (AdminClubMemberDto) o;
        return newMemberId.equals(that.newMemberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newMemberId);
    }
}
