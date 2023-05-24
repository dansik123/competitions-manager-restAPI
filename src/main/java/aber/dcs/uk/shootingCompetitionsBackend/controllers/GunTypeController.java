package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.services.GunTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/gun-types")
public class GunTypeController {
    private final GunTypeService gunTypeService;

    public GunTypeController(GunTypeService gunTypeService) {
        this.gunTypeService = gunTypeService;
    }

    /**
     * HTTP get method to get list of gunTypes provided by API
     * @return HTTP response with JSON gunTypes array
     */
    @GetMapping
    public ResponseEntity<List<String>> getAllGunTypes(){
        return new ResponseEntity<>(
                gunTypeService.getAvailableGunTypes(),
                HttpStatus.OK);
    }
}
