package aber.dcs.uk.shootingCompetitionsBackend.services;

import aber.dcs.uk.shootingCompetitionsBackend.dao.LeagueRelocateUsersDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.RoundPauseUserDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.ShootingSlotDao;
import aber.dcs.uk.shootingCompetitionsBackend.dao.UserMemberDao;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.ImageMediaDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.ShootingResultDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.admin.MatchDateDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.LeagueEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingPauseSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.ShootingSlotEntity;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.exceptions.CustomHttpException;
import aber.dcs.uk.shootingCompetitionsBackend.repositories.*;
import aber.dcs.uk.shootingCompetitionsBackend.responses.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaguesMatchesService {
    private final LeagueRepository leagueRepository;
    private final LeagueCompetitorsRepository leagueCompetitorsRepository;
    private final ShootingSlotRepository shootingSlotRepository;

    private final ShootingPauseSlotRepository shootingPauseSlotRepository;

    private final MatchSlotPointsService matchSlotPointsService;

    private final ScoreCardsImagesService scoreCardsImagesService;

    private final static String IMAGE_PNG_SUFFIX = ".png";
    private final static List<String> ALLOW_IMAGE_SUFFIX = new ArrayList<>(List.of(".jpg",".jpeg",".png"));


    public LeaguesMatchesService(
            LeagueRepository leagueRepository,
            ShootingSlotRepository shootingSlotRepository,
            LeagueCompetitorsRepository leagueCompetitorsRepository,
            ShootingPauseSlotRepository shootingPauseSlotRepository,
            MatchSlotPointsService matchSlotPointsService, ScoreCardsImagesService scoreCardsImagesService) {
        this.leagueRepository = leagueRepository;
        this.shootingSlotRepository = shootingSlotRepository;
        this.leagueCompetitorsRepository = leagueCompetitorsRepository;
        this.shootingPauseSlotRepository = shootingPauseSlotRepository;
        this.matchSlotPointsService = matchSlotPointsService;
        this.scoreCardsImagesService = scoreCardsImagesService;
    }

    /**
     * ADMIN ACCESS ONLY
     * Method generate matches for league using round-robin match scheduling
     * @param leagueId Long the id of league
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void generateLeagueMatches(Long leagueId){
        LeagueEntity leagueEntity = leagueRepository.findById(leagueId).orElseThrow(
                ()-> new CustomHttpException(
                        String.format("League with following id %s does not exists", leagueId),
                        HttpStatus.NOT_FOUND
                ));

        if(leagueEntity.getHasGeneratedMatches()){
            throw new CustomHttpException(
                    String.format("League with id %s already have matches generated", leagueId),
                    HttpStatus.CONFLICT
            );
        }
        List<Long> leagueCompetitorsList = leagueCompetitorsRepository.getLeagueCompetitorsIdsByLeagueId(leagueId);

        if(leagueCompetitorsList.size() % 2 != 0){ //add null user to  create shooting slots from even number of users
            leagueCompetitorsList.add(null);
        }

        leagueEntity.setHasGeneratedMatches(true);
        LeagueEntity updatedLeague = leagueRepository.save(leagueEntity);
        roundRobinGenerateShootingSlotEntities(updatedLeague, leagueCompetitorsList);
    }

    /**
     * Method schedules/pairs competitors to generate list of matches for league
     * @param leagueEntity league database entity
     * @param leagueCompetitorsList list of competitors related with league Entity
     */
    private void roundRobinGenerateShootingSlotEntities(
            LeagueEntity leagueEntity,
            List<Long> leagueCompetitorsList) {
        int leaguesTableSize = leagueCompetitorsList.size();
        int middleTableIndex = leaguesTableSize / 2;
        List<ShootingSlotEntity> toSaveShootingSlots = new ArrayList<>();
        List<ShootingPauseSlotEntity> toSaveShootingPauseSlots = new ArrayList<>();

        //move elements clock wise in table to generate new matches between users
        //each element in table will move except the fist element
        for(int i=0; i < leaguesTableSize - 1; i++){
            Long middleCircleToEndPosition =
                leagueCompetitorsList.remove(middleTableIndex - 1 );
            Long middleCircleToSecondPosition =
                    leagueCompetitorsList.remove(middleTableIndex -1 );

            leagueCompetitorsList.add(middleCircleToEndPosition);
            leagueCompetitorsList.add(1, middleCircleToSecondPosition);

            //generate slots from list by taking a pair of users
            for(int b=0; b < middleTableIndex; b++){
                Long competitor1Id = leagueCompetitorsList.get(b);
                Long competitor2Id = leagueCompetitorsList.get(middleTableIndex + b);
                boolean hasOpponent;
                UserEntity competitor1Entity;
                UserEntity competitor2Entity = null;

                if(competitor1Id != null && competitor2Id != null){
                    hasOpponent = true;
                    competitor1Entity = new UserEntity(competitor1Id);
                    competitor2Entity = new UserEntity(competitor2Id);
                }else if(competitor1Id == null){
                    hasOpponent = false;
                    competitor1Entity = new UserEntity(competitor2Id);
                }else{
                    hasOpponent = false;
                    competitor1Entity = new UserEntity(competitor1Id);
                }

                if(hasOpponent){
                    //save shooting slot where there is 2 competitors
                    ShootingSlotEntity shootingSlot = new ShootingSlotEntity(
                            null, leagueEntity,
                            competitor1Entity, competitor2Entity,
                            null, leagueEntity.getCurrentRound(), i+1,  false,
                            null, null,
                            null, null
                    );
                    toSaveShootingSlots.add(shootingSlot);
                }else{
                    //save shooting pause slot where there is 1 competitor
                    ShootingPauseSlotEntity shootingRoundWaitSlot = new ShootingPauseSlotEntity(
                            null, leagueEntity, competitor1Entity, leagueEntity.getCurrentRound(), i+1
                    );
                    toSaveShootingPauseSlots.add(shootingRoundWaitSlot);
                }
            }
        }
        if(!toSaveShootingSlots.isEmpty()) {
            List<ShootingSlotEntity> savedSlots = shootingSlotRepository.saveAll(toSaveShootingSlots);
            matchSlotPointsService.generateSlotsShootingPoints(savedSlots, leagueEntity);
        }
        if(!toSaveShootingPauseSlots.isEmpty()) {
            shootingPauseSlotRepository.saveAll(toSaveShootingPauseSlots);
        }
    }

    /**
     * Method generate list of single league round matches for league by grouping them by Match rounds
     * @param leagueId league identifier by which matches are selected
     * @return list of single league round matches in league which includes information
     * who is not playing in each match round
     */
    public List<SingleRoundMatchesResponse> getMatchesGroupsBySlotRound(Long leagueId){
        List<SingleRoundMatchesResponse> leagueAllRoundsMatches = new ArrayList<>();
        Integer leagueCurrentRound = leagueRepository
                .findLeagueCurrentRoundIdByLeagueId(leagueId)
                .orElseThrow(
                    () -> new CustomHttpException(
                            String.format("League with id %s might not exist or league round is null", leagueId),
                            HttpStatus.NOT_FOUND
                    ));
        List<ShootingSlotDao> shootingSlots =
                shootingSlotRepository.findAllByLeagueAndHasOpponentOrderByRoundNumberAsc(
                        leagueId, leagueCurrentRound);
        List<RoundPauseUserDao> shootingSlotsWithPausedUsers =
                shootingPauseSlotRepository.findAllUsersOnPauseForLeagueOrderByRoundNumberAsc(
                        leagueId, leagueCurrentRound);

        while(!shootingSlots.isEmpty()){ //remove all slots grouped by round number from list
            int roundNo = shootingSlots.get(0).getRoundNumber();
            //get round matches with the same round number(data is ordered by roundNumber from 1 to N)
            List<ShootingSlotDao> singleRoundMatches = shootingSlots.stream().
                    takeWhile(slot -> slot.getRoundNumber().equals(roundNo)).
                    toList();

            List<MatchResponse> matchResponses =
                    singleRoundMatches.stream().map(MatchResponse::new).toList();

            SingleRoundMatchesResponse leagueRoundMatchesResponse;

            //if there are some users who don't play in this round and round number for all group of paused users
            //is the same as round number which we now create group then create group with paused user's in it
            if(!shootingSlotsWithPausedUsers.isEmpty() &&
                    shootingSlotsWithPausedUsers.get(0).getRoundNumber().equals(roundNo)){
                List<RoundPauseUserDao> pauseUsers = shootingSlotsWithPausedUsers.stream().takeWhile(
                        slot -> slot.getRoundNumber().equals(roundNo)).toList();
                ShootingSlotDao roundFirstSlot = shootingSlots.get(0);

                leagueRoundMatchesResponse = new SingleRoundMatchesResponse(
                        roundFirstSlot.getRoundNumber(),
                        pauseUsers.stream().map(
                                roundPauseUserDao -> new UserMemberResponse(roundPauseUserDao.pausedUser())
                        ).toList(),
                        matchResponses
                );
                shootingSlotsWithPausedUsers.removeAll(pauseUsers);
            }else{
                //otherwise create round group of slots where there is no users on pause in this round
                ShootingSlotDao roundFirstSlot = shootingSlots.get(0);
                leagueRoundMatchesResponse = new SingleRoundMatchesResponse(
                        roundFirstSlot.getRoundNumber(),
                        new ArrayList<>(),
                        matchResponses
                );
            }

            shootingSlots.removeAll(singleRoundMatches);
            leagueAllRoundsMatches.add(leagueRoundMatchesResponse);
        }

        return leagueAllRoundsMatches;
    }

    /**
     * Method selects single match by id
     * @param matchId Long match identifier
     * @return SingleMatch data
     */
    public SingleMatchResponse getSingleMatch(Long matchId){
        ShootingSlotEntity foundSlot =  shootingSlotRepository.findById(matchId).orElseThrow(
                () -> new CustomHttpException(
                        String.format("Match with id %s does not exist", matchId),
                        HttpStatus.NOT_FOUND
                ));
        return new SingleMatchResponse(foundSlot);
    }

    /**
     * Method gets all user matches withing scope of the league
     * @param leagueId Long league identifier
     * @param userId Long user identifier
     * @return list of matches related with user
     */
    public List<SingleMatchResponse> getAllMatchesAssignedToUser(Long leagueId, Long userId){
        return shootingSlotRepository.findAllMatchesWithUserIdAndHasOpponent(leagueId, userId).
                stream().
                map(SingleMatchResponse::new).
                collect(Collectors.toList());
    }

    /**
     * ADMIN ACCESS ONLY
     * Method changes match date
     * @param matchId Long match identifier
     * @param dateDto new date information
     * @return Single match object with updated date
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public SingleMatchResponse changeMatchDate(Long matchId, MatchDateDto dateDto) {
        ShootingSlotEntity shootingSlotEntity = shootingSlotRepository.findById(matchId).orElseThrow(
                () -> new CustomHttpException(
                        String.format("Match with id %s does not exists", matchId),
                        HttpStatus.NOT_FOUND
                ));
        shootingSlotEntity.setSlotDate(Date.valueOf(dateDto.getNewDate()));

        ShootingSlotEntity savedSlot = shootingSlotRepository.save(shootingSlotEntity);
        return new SingleMatchResponse(savedSlot);
    }

    /**
     * ADMIN ACCESS ONLY
     * Method deletes all matches in league and related data
     * Pause users, matches, matches points.
     * Then updates league to allow for generation of new matches
     * League round number is reset to 1
     * @param leagueId league identifier
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void deleteAllLeagueMatches(Long leagueId) {
        matchSlotPointsService.deleteShootingSlotsRelatedPointsByLeagueId(leagueId);
        shootingSlotRepository.deleteAllSlotsByLeagueId(leagueId);
        shootingPauseSlotRepository.deleteAllPauseSlotsByLeagueId(leagueId);
        leagueRepository.updateLeagueMatchesGeneratedToFalse(leagueId);
    }

    /**
     * ADMIN ACCESS ONLY
     * Methods deletes all matches and matches related data in league for single user
     * Pause users, matches, matches points.
     * @param leagueId league identifier
     * @param userId user identifier
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void deleteAllUserMatches(Long leagueId, Long userId) {
        List<ShootingSlotEntity> slotsCompetitor1 =
                shootingSlotRepository.findByLeagueAndCompetitor1(
                        new LeagueEntity(leagueId),
                        new UserEntity(userId));
        List<ShootingSlotEntity> slotsCompetitor2 =
                shootingSlotRepository.findByLeagueAndCompetitor2(
                        new LeagueEntity(leagueId),
                        new UserEntity(userId));
        List<ShootingPauseSlotEntity> pauseSlots = new LinkedList<>();
        //create pauseSlots for all slots where user was as competitor1
        // competitor 2 is moved to pause slot in this case
        for(ShootingSlotEntity shootingSlotEntity: slotsCompetitor1){
            ShootingPauseSlotEntity newPauseSlot = new ShootingPauseSlotEntity();
            newPauseSlot.setLeague(new LeagueEntity(leagueId));
            newPauseSlot.setCompetitor(shootingSlotEntity.getCompetitor2());
            newPauseSlot.setMatchRoundNumber(shootingSlotEntity.getMatchRoundNumber());
            newPauseSlot.setLeagueRoundNumber(shootingSlotEntity.getLeagueRoundNumber());
            pauseSlots.add(newPauseSlot);
        }

        //create pauseSlots for all slots where user was as competitor2
        // competitor 1 is moved to pause slot in this case
        for(ShootingSlotEntity shootingSlotEntity: slotsCompetitor2){
            ShootingPauseSlotEntity newPauseSlot = new ShootingPauseSlotEntity();
            newPauseSlot.setLeague(new LeagueEntity(leagueId));
            newPauseSlot.setCompetitor(shootingSlotEntity.getCompetitor1());
            newPauseSlot.setMatchRoundNumber(shootingSlotEntity.getMatchRoundNumber());
            newPauseSlot.setLeagueRoundNumber(shootingSlotEntity.getLeagueRoundNumber());
            pauseSlots.add(newPauseSlot);
        }
        //delete all possible slots related with user
        shootingSlotRepository.deleteAll(slotsCompetitor1);
        shootingSlotRepository.deleteAll(slotsCompetitor2);
        shootingPauseSlotRepository.deleteAllPauseSlotsByLeagueIdAndUserId(leagueId,userId);

        //add new pause slots due to deletion of slots related with user
        shootingPauseSlotRepository.saveAll(pauseSlots);
    }

    /**
     * ADMIN AND SPECTATOR ACCESS ONLY
     * Methods updates match shooting results
     * @param matchId match identifier
     * @param shootingResults new shooting results
     * @return match object with updated results
     */
    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('SPECTATOR')")
    @Transactional
    public SingleMatchResponse updateSlotWithShootingResult(Long matchId, ShootingResultDto shootingResults){
        ShootingSlotEntity slot = shootingSlotRepository.findById(matchId).orElseThrow(
                () -> new CustomHttpException(
                        String.format("Match with id %s does not exist", matchId),
                        HttpStatus.NOT_FOUND
                ));

        if(slot.getSlotDate() == null){
            throw new CustomHttpException(
                    String.format("Match with id %s must have date before adding results", matchId),
                    HttpStatus.CONFLICT);
        }
        slot.setCompetitor1Score(shootingResults.getCompetitor1Result());
        slot.setCompetitor2Score(shootingResults.getCompetitor2Result());
        slot.setHasScoreResult(true);
        ShootingSlotEntity savedSlot = shootingSlotRepository.save(slot);
        matchSlotPointsService.updateSlotShootingPoints(savedSlot);
        return new SingleMatchResponse(savedSlot);
    }

    /**
     * Method produce league group summary by getting all matches and total league points
     * @param leaguesIds list of leagues identifiers
     * @return league group summary list data (individual matches info with total league points)
     */
    public List<SummaryPointsRowResponse<LeagueRoundSingleSlotPoints>> getAllLeaguesGroupStats(
            List<Long> leaguesIds){
        List<UserMemberDao> leagueUsers =
                leagueCompetitorsRepository.getAllLeaguesCompetitors(leaguesIds);

        return matchSlotPointsService.generateSummaryAllLeaguesGroupTable(leagueUsers, leaguesIds);
    }

    /**
     * Method gets summary of one league round matches in one league
     * @param leagueId league identifier
     * @return league summary list data (individual matches info with total league points[FOR ALL LEAGUE ROUNDS])
     */
    public List<SummaryPointsRowResponse<MatchRoundSingleSlotPoints>> getLeagueStats(Long leagueId){
        Boolean areMatchesGenerated =
                leagueRepository.getValueAreLeagueMatchesGeneratedByLeagueId(leagueId).orElseThrow(
                        () -> new CustomHttpException(
                        String.format("League with id %s does not exist", leagueId),
                        HttpStatus.NOT_FOUND)
                );

        if(!areMatchesGenerated){
            throw new CustomHttpException(
                    String.format("League with id %s don't have generated matches", leagueId),
                    HttpStatus.CONFLICT);
        }

        Integer leagueCurrentRound = leagueRepository
                .findLeagueCurrentRoundIdByLeagueId(leagueId)
                .orElseThrow(
                        () -> new CustomHttpException(
                                String.format("League with id %s might not exist or league round is null", leagueId),
                                HttpStatus.NOT_FOUND
                        ));
        List<UserMemberDao> leagueUsers =
                leagueCompetitorsRepository.getLeagueCompetitorsByLeagueId(leagueId);

        return matchSlotPointsService.generateSummaryOneLeaguesTable(leagueId, leagueCurrentRound, leagueUsers);
    }

    /**
     * ADMIN AND SPECTATOR ACCESS ONLY
     * Method saves shooting scorecard image
     * @param matchId match identifier
     * @param isCompetitor1 which competitor in match we assign the picture
     * @param multipartFile image data
     * @return match object with image name of score-card
     * @throws IOException if there is problem to store image file
     */
    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('SPECTATOR')")
    public SingleMatchResponse addImageToCompetitor(
            Long matchId, Boolean isCompetitor1, MultipartFile multipartFile) throws IOException {
        String filename = multipartFile.getOriginalFilename();
        if(ALLOW_IMAGE_SUFFIX.stream().noneMatch(suffix -> Objects.requireNonNull(filename).endsWith(suffix))){
            throw new CustomHttpException(
                    "Not an image file upload only JPG and PNG allowed",
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        ShootingSlotEntity shootingSlotEntity;
        shootingSlotEntity = shootingSlotRepository.findById(matchId).orElseThrow(
                () -> new CustomHttpException(
                        String.format("Image for match id %s does not exist", matchId),
                        HttpStatus.NOT_FOUND)
        );

        String savedFilename = scoreCardsImagesService.storeMultiPartImage(multipartFile);

        if(isCompetitor1) {
            shootingSlotEntity.setCompetitor1ScoreCardLink(savedFilename);
        }else{
            shootingSlotEntity.setCompetitor2ScoreCardLink(savedFilename);
        }

        return new SingleMatchResponse(shootingSlotRepository.save(shootingSlotEntity));
    }

    /**
     * Method reads match image of score-card, it includes image type as HTTP header
     * @param matchId match identifier
     * @param isCompetitor1 which competitor in match we assign the picture
     * @return Image data with image type stores as HTTP header string
     * @throws IOException there is problem to read file
     */
    public ImageMediaDto getImageForMatchAndCompetitor(Long matchId, Boolean isCompetitor1) throws IOException{
        ImageMediaDto imageMediaDto = new ImageMediaDto();
        Optional<String> shootingImageFileName;
        if(isCompetitor1) {
            shootingImageFileName = shootingSlotRepository.findCompetitor1MatchImage(matchId);
        }else{
            shootingImageFileName = shootingSlotRepository.findCompetitor2MatchImage(matchId);
        }

        if(shootingImageFileName.isEmpty()){
            throw new CustomHttpException(
                    String.format("Image for match id %s does not exist", matchId),
                    HttpStatus.NOT_FOUND);
        }
        String imageFileName = shootingImageFileName.get();
        if(imageFileName.endsWith(IMAGE_PNG_SUFFIX)){
            imageMediaDto.setMediaType(MediaType.IMAGE_PNG);
        }else{
            imageMediaDto.setMediaType(MediaType.IMAGE_JPEG);
        }

        imageMediaDto.setImageContent(
                scoreCardsImagesService.readStoredScoreCardImage(imageFileName));
        return imageMediaDto;
    }

    /**
     * ADMIN ACCESS ONLY
     * Method relocates/swaps users between leagues
     * @param leaguePrefix league group name prefix
     * @param leagueIds list of league identifiers in which relocation will take place
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void relocateAndPromotionsProcedure(String leaguePrefix, List<Long> leagueIds){
        List<LeagueEntity> prefixedNamedLeagues = leagueRepository
                .findByNameLikeOrderByIdAsc(leaguePrefix + "%");
        if(prefixedNamedLeagues.stream().anyMatch(league -> league.getCurrentRound() >= league.getTotalRoundsToPlay())){
            throw new CustomHttpException(
                    "Can't relocate users because this was final round for some or all leagues",
                    HttpStatus.CONFLICT
            );
        }
        List<Long> prefixedNamedLeaguesIds = prefixedNamedLeagues
                .stream()
                .map(LeagueEntity::getId)
                .toList();
        Set<Long> leagueIdsSet = new HashSet<>(leagueIds);

        if(!leagueIdsSet.containsAll(prefixedNamedLeaguesIds)){
            throw new CustomHttpException(
                    String.format("Please select all leagues in the group %s to start relocate", leaguePrefix),
                    HttpStatus.CONFLICT
            );
        }

        //it is safe to start relocation
        relocationAndPromotionForAllLeagues(prefixedNamedLeagues);
    }

    /**
     * Methods relocate competitors in leagues
     * and updates data in league like increasing league round number
     * and matchesGenerated boolean to allow to generate matches for new round
     * @param leagues leagues database entity data list
     */
    @Transactional
    public void relocationAndPromotionForAllLeagues(List<LeagueEntity> leagues){
        if(leagues.size() < 1){
            // 0 leagues means there is noting to do
            return;
        }
        if(leagues.size() < 2){
            LeagueEntity singleLeague = leagues.get(0);
            singleLeague.setHasGeneratedMatches(false);
            singleLeague.setCurrentRound(singleLeague.getCurrentRound() + 1);
            leagueRepository.save(singleLeague);
            return;
        }

        // all other cases with array >= 2
        LeagueEntity league1;
        LeagueEntity league2 = new LeagueEntity(-1L);
        int leaguePairCompetitorsSum;
        List<LeagueRelocateUsersDao> relocateUsers;
        for(int i=1; i < leagues.size(); i++){
            league1 = leagues.get(i-1);
            league2 = leagues.get(i);
            leaguePairCompetitorsSum = league1.getNumberOfCompetitors() +
                    league2.getNumberOfCompetitors();
            //there we can provide more if statement to relocate more users per league
            //depending on number of users in both leagues we move accordingly 2,1 or 0 users
            if(leaguePairCompetitorsSum >= 10){ // 10..n users count from both leagues
                relocateUsers = matchSlotPointsService.getUsersToRelocate(
                        league1.getId(), league2.getId(), 2);
                relocateAndPromoteLeaguePair(relocateUsers, 2,
                        league1.getId(), league2.getId());

                //league with relocated users can now regenerate matches for next round
                league1.setHasGeneratedMatches(false);
                league1.setCurrentRound(league1.getCurrentRound() + 1);
                leagueRepository.save(league1);
            }else if(leaguePairCompetitorsSum >= 3){ // 3..9 users count from both leagues
                relocateUsers = matchSlotPointsService.getUsersToRelocate(
                        league1.getId(), league2.getId(), 1);
                relocateAndPromoteLeaguePair(relocateUsers, 1,
                        league1.getId(), league2.getId());

                //league with relocated users can now regenerate matches for next round
                league1.setHasGeneratedMatches(false);
                league1.setCurrentRound(league1.getCurrentRound() + 1);
                leagueRepository.save(league1);
            }
        }

        league2.setHasGeneratedMatches(false);
        league2.setCurrentRound(league2.getCurrentRound() + 1);
        leagueRepository.save(league2);
    }

    /**
     * Method relocate all matches related data for users between two leagues
     * @param relocateUsers list of users to relocate data top list users are relocated
     *                      to second league and bottom list users are relocated to first league
     * @param numberOfUserToRelocatePerLeague how many users we relocate
     * @param league1Id top/first league identifier
     * @param league2Id bottom/second league identifier
     */
    public void relocateAndPromoteLeaguePair(
            List<LeagueRelocateUsersDao> relocateUsers, int numberOfUserToRelocatePerLeague,
            Long league1Id, Long league2Id){
        //league 1 bottom user looking at summary scores
        List<LeagueRelocateUsersDao>league1IdBottomUsers =
                relocateUsers.subList(0, numberOfUserToRelocatePerLeague);
        //league 2 top user looking at summary scores
        List<LeagueRelocateUsersDao>league2IdTopUsers =
                relocateUsers.subList(numberOfUserToRelocatePerLeague, relocateUsers.size());
        //relegate
        for(LeagueRelocateUsersDao league1BottomUser: league1IdBottomUsers){
            //move user's points to lower league
            matchSlotPointsService.moveUserPointsToDifferentLeague(
                    league2Id,
                    league1BottomUser.getLeagueId(),
                    league1BottomUser.getPointsOwnerId());

            //move user's match pause slots to lower league
            shootingPauseSlotRepository.updateLeagueIdForOneUserAllPauseSlots(
                    league2Id,
                    league1BottomUser.getLeagueId(),
                    league1BottomUser.getPointsOwnerId());

            //move user's match slots to lower league
            shootingSlotRepository.updateLeagueIdForOneUserAllMatchSlots(
                    league2Id,
                    league1BottomUser.getLeagueId(),
                    league1BottomUser.getPointsOwnerId());

            //move user's assignment to lower league
            leagueCompetitorsRepository.updateLeagueIdForOneUser(
                    league2Id,
                    league1BottomUser.getLeagueId(),
                    league1BottomUser.getPointsOwnerId());
        }

        //promote
        for(LeagueRelocateUsersDao league2TopUser: league2IdTopUsers){

            //move user's points to upper league
            matchSlotPointsService.moveUserPointsToDifferentLeague(
                    league1Id,
                    league2TopUser.getLeagueId(),
                    league2TopUser.getPointsOwnerId());

            //move user's match pause slots to upper league
            shootingPauseSlotRepository.updateLeagueIdForOneUserAllPauseSlots(
                    league1Id,
                    league2TopUser.getLeagueId(),
                    league2TopUser.getPointsOwnerId());

            //move user's match slots to upper league
            shootingSlotRepository.updateLeagueIdForOneUserAllMatchSlots(
                    league1Id,
                    league2TopUser.getLeagueId(),
                    league2TopUser.getPointsOwnerId());

            //move user's assignment to upper league
            leagueCompetitorsRepository.updateLeagueIdForOneUser(
                    league1Id,
                    league2TopUser.getLeagueId(),
                    league2TopUser.getPointsOwnerId());
        }
    }
}
