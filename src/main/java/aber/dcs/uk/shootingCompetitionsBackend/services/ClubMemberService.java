package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.dao.ClubDao;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.AdminClubMemberDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.NewClubDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ClubMemberEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.CustomHttpException;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.ClubMemberRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.ClubRepository;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.UserRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.GeneralResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserMemberResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.admin.ClubMemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubMemberService {
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    public ClubMemberService(ClubMemberRepository clubMemberRepository,
                             UserRepository userRepository, ClubRepository clubRepository) {
        this.clubMemberRepository = clubMemberRepository;
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
    }

    /**
     * Service method to get limited list of club members as page
     * @param clubId club id
     * @param pageable object contains page number, page size and page order
     * @return Page with list of club members
     */
    public Page<UserMemberResponse> getAllMembersOfClub(Long clubId, Pageable pageable){
        return clubMemberRepository.getUsersByClubId(clubId, pageable).
                map(UserMemberResponse::new);
    }

    /**
     * ADMIN ACCESS ONLY
     * Service method to add new user to club as member
     * if user was assigned to other club then new  club information will be overwritten
     * @param clubId club id
     * @param clubMemberDto object contains user data required to assign it for club
     * @throws CustomHttpException if club or user does not exist
     * @return object with details about new member of the club
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public ClubMemberResponse addNewClubMember(Long clubId, AdminClubMemberDto clubMemberDto)
            throws CustomHttpException{
        UserEntity user = userRepository.findById(clubMemberDto.getNewMemberId()).
            orElseThrow(() -> new CustomHttpException(
                String.format("User with id %s does not exists", clubMemberDto.getNewMemberId()),
                HttpStatus.NOT_FOUND));

        ClubEntity clubEntity = clubRepository.findById(clubId).orElseThrow(
                () -> new CustomHttpException(
                        String.format("Club with id %s does not exist", clubId),
                        HttpStatus.NOT_FOUND)
        );

        ClubMemberEntity clubMember = new ClubMemberEntity(user.getId(), user, clubEntity);
        ClubMemberEntity savedClubMember = clubMemberRepository.save(clubMember);
        return new ClubMemberResponse(savedClubMember);
    }

    /**
     * ADMIN ACCESS ONLY
     * Method change user existing club to new other one saved in system
     * @param clubId member existing club
     * @param memberId member id
     * @param newClub new changed club
     * @throws CustomHttpException if association between user and club is not correct or new club does not exist
     * @return object with details about new member of the club
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public ClubMemberResponse changeMemberClub(Long clubId, Long memberId, ClubEntity newClub)
            throws CustomHttpException{
        ClubMemberEntity savedClubMember = clubMemberRepository.findById(memberId).orElseThrow(
                () -> new CustomHttpException(
                    String.format("Club member with id %s does not exists", memberId),
                    HttpStatus.CONFLICT)
        );

        if(!savedClubMember.getClub().getId().equals(clubId)){
            throw new CustomHttpException(
                String.format("Club member with club id %s does not exists", clubId),
                HttpStatus.CONFLICT);
        }

        if(!clubRepository.existsByIdAndClubName(newClub.getId(), newClub.getClubName())){
            throw new CustomHttpException(
                    String.format("Club with id %s and name %s does not exist",
                            newClub.getId(), newClub.getClubName()),
                    HttpStatus.CONFLICT);
        }

        savedClubMember.setClub(newClub);

        ClubMemberEntity updatedClubMember = clubMemberRepository.save(savedClubMember);
        return new ClubMemberResponse(updatedClubMember);
    }

    /**
     * ADMIN ACCESS ONLY
     * Method deletes existing club member
     * @param clubId existing club id
     * @param userId existing club member id
     * @throws CustomHttpException if association between user and club is not correct
     * @return object with message that delete was successful
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public GeneralResponse deleteClubMember(Long clubId, Long userId)
            throws CustomHttpException{
        ClubDao clubDao = clubMemberRepository.getClubByUserId(userId).orElseThrow(
                () -> new CustomHttpException(
                        String.format("Club member with id %s is not found", userId),
                        HttpStatus.NOT_FOUND)
        );
        if(!clubDao.getId().equals(clubId)){
            throw new CustomHttpException(String.format(
                    "Club member with id %s is not assigned to club with id %s",
                    userId, clubId),
                    HttpStatus.NOT_FOUND);
        }
        clubMemberRepository.deleteById(userId);
        return new GeneralResponse(String.format("Club member with id %s has been removed from club \"%s\"",
                userId, clubDao.getClubName()));
    }

    /**
     * Method gets list of database clubs
     * @return List of ClubEntity
     */
    public List<ClubEntity> getListOfClubs(){
        return clubRepository.findAll();
    }

    /**
     * ADMIN ACCESS ONLY
     * Method adds new club to database
     * @param newClub new club data
     * @return database saved new club
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public ClubEntity addNewClub(NewClubDto newClub){
        return clubRepository.save(new ClubEntity(null, newClub.getNewClubName()));
    }

    /**
     * ADMIN ACCESS ONLY
     * Method changes existing name of the club
     * @param id existing club id
     * @param newClub new club data
     * @return database changed club
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public ClubEntity editClub(Long id, NewClubDto newClub){
        ClubEntity existingClub = clubRepository.findById(id).orElseThrow(
                () -> new CustomHttpException(
                        String.format("Club with id %s not found", id),
                        HttpStatus.NOT_FOUND
                )
        );
        existingClub.setClubName(newClub.getNewClubName());
        return clubRepository.save(existingClub);
    }

    /**
     * ADMIN ACCESS ONLY
     * Method deletes club by existing club id
     * @param id club id
     * @return GeneralResponse object with message about successful execution
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public GeneralResponse deleteClub(Long id){
        if(!clubRepository.existsById(id)){
            throw new CustomHttpException(
                    String.format("Club with id %s not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
        clubRepository.deleteById(id);
        return new GeneralResponse(
                String.format("Club with id %s has been deleted", id));
    }
}
