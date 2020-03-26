package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;


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
  
  
    @PostMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public TournamentDto createTournament(@PathVariable int executionId, @RequestBody TournamentDto tournamentDto){
        logger.debug("createTournament");
        return tournamentService.createTournament(executionId, tournamentDto);
    }

  
  

}

