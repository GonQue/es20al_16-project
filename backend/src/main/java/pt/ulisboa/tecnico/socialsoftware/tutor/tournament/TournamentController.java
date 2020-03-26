package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

@RestController
public class TournamentController {
    private static Logger logger = LoggerFactory.getLogger(TournamentController.class);

    @Autowired
    private TournamentService tournamentService;


    @PostMapping("/tournaments/{tournamentId}/enroll-student")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public TournamentDto studentEnrollTournament(@PathVariable Integer tournamentId, @RequestBody UserDto userDto){
        logger.debug("createTournament");
        return tournamentService.enrollStudent(tournamentId, userDto);
    }




}