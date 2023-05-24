package aber.dcs.uk.shootingCompetitionsBackend.controllers;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.RegisterUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.responses.GeneralResponse;
import aber.dcs.uk.shootingCompetitionsBackend.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/register")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * HTTP Post method, it registers new user in the system
     * @param newUser new user details
     * @return Ok response with message information about successful registration
     * or error message with information what went wrong
     */
    @PostMapping
    ResponseEntity<GeneralResponse> registerNewUser(
            @Valid @RequestBody RegisterUserDto newUser){
        return new ResponseEntity<>(
                registrationService.signUpUser(newUser), HttpStatus.CREATED);
    }
}
