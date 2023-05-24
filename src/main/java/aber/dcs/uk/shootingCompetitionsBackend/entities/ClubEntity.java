package aber.dcs.uk.shootingCompetitionsBackend.entities;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ClubDao;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "clubs")
public class ClubEntity implements ClubDao {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "club_name")
    private String clubName;

    public ClubEntity() {
    }

    public ClubEntity(Long id) {
        this.id = id;
    }

    public ClubEntity(Long id, String name) {
        this.id = id;
        this.clubName = name;
    }

    public ClubEntity(ClubDao clubDao){
        this.id = clubDao.getId();
        this.clubName = clubDao.getClubName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubEntity that = (ClubEntity) o;
        return id.equals(that.id) && clubName.equals(that.clubName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clubName);
    }
}
