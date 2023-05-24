package aber.dcs.uk.shootingCompetitionsBackend.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "club_members",
        uniqueConstraints = { @UniqueConstraint( name="UNIQUE_CLUB_MEMBERS",
        columnNames = {"member_id", "club_id"}) })
public class ClubMemberEntity {
    @Id
    @Column(name="member_id")
    private Long userId;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id",
            foreignKey = @ForeignKey(name = "FK_CLUB_MEMBER_LINKS_USER"))
    private UserEntity member;

    @ManyToOne
    @JoinColumn(name="club_id",
            foreignKey = @ForeignKey(name = "FK_CLUB_MEMBER_LINKS_CLUB"))
    private ClubEntity club;

    public ClubMemberEntity() {
    }

    public ClubMemberEntity(
            Long userId, UserEntity member, ClubEntity club) {
        this.userId = userId;
        this.member = member;
        this.club = club;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserEntity getMember() {
        return member;
    }

    public void setMember(UserEntity member) {
        this.member = member;
    }

    public ClubEntity getClub() {
        return club;
    }

    public void setClub(ClubEntity club) {
        this.club = club;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubMemberEntity that = (ClubMemberEntity) o;
        return userId.equals(that.userId) && member.equals(that.member) && club.equals(that.club);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, member, club);
    }
}
