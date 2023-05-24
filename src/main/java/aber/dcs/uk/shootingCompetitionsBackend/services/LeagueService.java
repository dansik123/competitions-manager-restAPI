package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.SaveLeagueDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.UpdateLeagueDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueCompetitorsEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.CustomHttpException;
import aber.dcs.uk.shootingCompetitionsBackend.models.GunType;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.LeagueRepository;
import aber.dcs.uk.shootingCompetitionsBackend.responses.LeagueGroupRelocationResponse;
import aber.dcs.uk.shootingCompetitionsBackend.responses.UserLeagueResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class LeagueService {
    private final LeagueRepository leagueRepository;
    private final LeagueGroupService leagueGroupService;

    public LeagueService(LeagueRepository leagueRepository, LeagueGroupService leagueGroupService) {
        this.leagueRepository = leagueRepository;
        this.leagueGroupService = leagueGroupService;
    }

    /**
     * Method to display page of existing leagues
     * @param pageable choose the page for league to display (page, size and resultSorting)
     * @return Page with league response data
     */
    public Page<UserLeagueResponse> getLeaguesListPage(Pageable pageable){
        return leagueRepository.findAll(pageable).map(UserLeagueResponse::new);
    }

    /**
     * Method displays information about single league
     * @param leagueId league id
     * @return UserLeagueResponse of single league
     */
    public UserLeagueResponse getSingleLeague(Long leagueId){
        return new UserLeagueResponse(
            leagueRepository.findById(leagueId).orElseThrow(
                () -> new CustomHttpException(
                    String.format("League with id %s was not found", leagueId),
                    HttpStatus.NOT_FOUND
                )
            )
        );
    }


    /**
     * ADMIN ACCESS ONLY
     * Method saves leagues groups and individual league competitors to database
     * @param leaguesGroups league groups data.
     *                      Contains information about league and their competitors
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void createLeaguesWithCompetitors(SaveLeagueDto leaguesGroups) {
        List<String> newLeaguesNames = leaguesGroups.getLeaguesGroups().keySet().stream().toList();
        boolean anyLeagueHaveNotRightNumberOfUsers =
            leaguesGroups.getLeaguesGroups().values().stream().
                    anyMatch(usersList -> usersList.size() > leaguesGroups.getLeagueMaxCompetitors());

        if(anyLeagueHaveNotRightNumberOfUsers){
            //each league can't have more competitors that it is
            // declared in league max competitors column
            throw new CustomHttpException(
                    "One or more leagues have to many competitors" +
                    "Or some leagues don't have any competitors" +
                    "Please increase leagueMaxCompetitors field" +
                    "or remove some users from this league" +
                    "Operation unsuccessful",
                    HttpStatus.CONFLICT
            );
        }


        if(leaguesGroups.getLeaguesGroups().values().stream().anyMatch(
                usersIds -> usersIds.isEmpty() || usersIds.size() < 2)){
            throw new CustomHttpException(
                    "You can't create league with 0 or 1 competitor",
                    HttpStatus.BAD_REQUEST);
        }


        if(!anyLeagueNameExistInDatabase(newLeaguesNames)) {
            //Create new league per new league name
            for (String leagueGroupName : newLeaguesNames) {
                List<Long> leagueCompetitorsList = leaguesGroups.getLeaguesGroups().get(leagueGroupName);

                //create new league Entity
                LeagueEntity newLeague = new LeagueEntity();
                newLeague.setMaxCompetitors(leaguesGroups.getLeagueMaxCompetitors());
                newLeague.setGunType(GunType.valueOf(leaguesGroups.getLeagueGunType()));
                newLeague.setName(leagueGroupName);
                newLeague.setHasGeneratedMatches(false);
                newLeague.setCurrentRound(1);
                newLeague.setTotalRoundsToPlay(leaguesGroups.getRoundsToPlay());
                newLeague.setNumberOfCompetitors(leagueCompetitorsList.size());
                LeagueEntity savedLeague = leagueRepository.save(newLeague);
                //Add League Competitors to league
                saveLeagueCompetitors(leagueCompetitorsList, savedLeague);
            }
        }
    }

    /**
     * Method checks if any league with name in database
     * have the same name in the given parameter list
     * @param newLeaguesNames List of new names to check
     * @return False if none of give leagues names are stored in database
     * @throws CustomHttpException when there is one or more league match name with the given list
     */
    private boolean anyLeagueNameExistInDatabase(List<String> newLeaguesNames) throws CustomHttpException{
        List<String> comparedLeagueNamesInDatabase =
                leagueRepository.getExistedLeaguesNamesByLeagueNamesList(newLeaguesNames);
        if(!comparedLeagueNamesInDatabase.isEmpty()){
            throw new CustomHttpException(
                String.format("List of the following leagues %s already exists in system" +
                        "please change their name to successfully save group of new leagues",
                        String.join(",", comparedLeagueNamesInDatabase)),
                HttpStatus.CONFLICT
            );
        }
        return false;
    }

    /**
     * Method saves information about league competitors
     * @param leagueCompetitorsList List of user's id's(They will be assigned to league)
     * @param savedLeague League Entity to which competitors are assigned to
     */
    private void saveLeagueCompetitors(
            List<Long> leagueCompetitorsList,
            LeagueEntity savedLeague
    ) {
        //Add and save competitors to league
        for(Long userId: leagueCompetitorsList){
            LeagueCompetitorsEntity competitorsEntity = new LeagueCompetitorsEntity();
            competitorsEntity.setLeagueId(savedLeague.getId());
            competitorsEntity.setLeague(savedLeague);
            competitorsEntity.setCompetitorId(userId);
            competitorsEntity.setCompetitor(new UserEntity(userId));
            savedLeague.getLeagueCompetitors().add(competitorsEntity);
        }

        leagueRepository.save(savedLeague);
    }

    /**
     * ADMIN ACCESS ONLY
     * Method updates existing league information
     * @param leagueId existing league id
     * @param leagueDto new league information(only league name is updatable)
     * @return updated league response
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserLeagueResponse updateLeague(Long leagueId, UpdateLeagueDto leagueDto){
        LeagueEntity existingLeague = leagueRepository.findById(leagueId).orElseThrow(
                () -> new CustomHttpException(
                        String.format("League with id %s does not exists", leagueId),
                        HttpStatus.NOT_FOUND
                ));

        existingLeague.setName(leagueDto.getNewLeagueName());
        LeagueEntity savedLeagueEntity = leagueRepository.save(existingLeague);
        return new UserLeagueResponse(savedLeagueEntity);
    }

    /**
     * ADMIN ACCESS ONLY
     * Method deletes existing league and related league competitors from db
     * @param leagueId existing league id
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteExistingLeague(Long leagueId){
        leagueRepository.deleteById(leagueId);
    }

    /**
     * Method gets gunType value assigned to league
     * @param leagueId league id
     * @return String with gunType stored in db
     */
    public String getGunTypeForLeague(Long leagueId){
        return leagueRepository.getLeagueGunTypeById(leagueId).orElseThrow(
            () -> new CustomHttpException(
                    String.format("League with id %s does not exists", leagueId),
                    HttpStatus.NOT_FOUND
            )
        );
    }

    /**
     * Method displays list of league groups
     * @param gunType name of gun type associated to league
     * @return league groups names grouped by leagues name prefix
     */
    public List<String> getLeagueAvailableGroups(String gunType){
        String capitalGunType = gunType.toUpperCase(Locale.ROOT);
        //regex patter to search all league names with the same gun type
        //original regex example: (.*)(%PISTOL_LEAGUE.*)
        String regex = String.format("(.*)(%s_LEAGUE.*)", capitalGunType);
        Integer regexPrefixGroupPosition = 1;
        List<String> allGunTypeLeaguesPrefixes = leagueRepository.leagueDistinctPrefixesByLeagueGunType(
                regex, regexPrefixGroupPosition);
        return allGunTypeLeaguesPrefixes.stream()
                .map(prefix -> String.format("%s%s_LEAGUE", prefix, capitalGunType))
                .collect(Collectors.toList());
    }

    /**
     * Method collects status of league group
     * @param leaguePrefix league group prefix name
     * @return object with information about status of league group as well as individual leagues in the group
     */
    public LeagueGroupRelocationResponse leagueGroupsRelocationStatus(String leaguePrefix){
        List<LeagueEntity> prefixedNamedLeagues = leagueRepository.findByNameLikeOrderByIdAsc(leaguePrefix + "%");
        return leagueGroupService.leaguesGroupStatus(prefixedNamedLeagues, leaguePrefix);
    }

    /**
     * Method updates league group all competitors average scores based on all shooting scores
     * stored in the league matches
     * @param leaguePrefix league group prefix
     */
    public void updateFinishedLeaguesGroupCompetitorsAverageScores(String leaguePrefix){
        List<LeagueEntity> prefixedNamedLeagues = leagueRepository.findByNameLikeOrderByIdAsc(leaguePrefix + "%");
        leagueGroupService.updateAverageScoreCompetitors(prefixedNamedLeagues, leaguePrefix);
    }
}
