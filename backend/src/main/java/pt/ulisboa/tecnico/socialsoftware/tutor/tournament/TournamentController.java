package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;


import java.util.List;
import java.security.Principal;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;



@RestController
public class TournamentController {
    private static Logger logger = LoggerFactory.getLogger(TournamentController.class);

    @Autowired
    private TournamentService tournamentService;



    @PostMapping("/tournaments/{tournamentId}/enroll-student")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public TournamentDto studentEnrollTournament(@PathVariable Integer tournamentId, @RequestBody UserDto userDto){
        return tournamentService.enrollStudent(tournamentId, userDto);
    }
  
  
    @PostMapping("/executions/{executionId}/create-tournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(Principal principal, @PathVariable int executionId, @RequestBody TournamentDto tournamentDto){
        logger.debug("createTournament");
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.createTournament(executionId, user.getId(), tournamentDto);
    }

    @GetMapping("/executions/{executionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> listOpenTournaments(@PathVariable int executionId) {
        return tournamentService.listOpenTournaments(executionId);
    }


}
