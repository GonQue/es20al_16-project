package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.security.Principal;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;



@RestController
public class TournamentController {
    private static Logger logger = LoggerFactory.getLogger(TournamentController.class);

    @Autowired
    private TournamentService tournamentService;



    @PostMapping("/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto studentEnrollTournament(@PathVariable int tournamentId, Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.enrollStudent(tournamentId, user.getId());
    }
  
  
    @PostMapping("/executions/{executionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(Principal principal, @PathVariable int executionId, @RequestBody TournamentDto tournamentDto){
        logger.debug("createTournament"+ tournamentDto.getId()+ tournamentDto.getName());
        User user = (User) ((Authentication) principal).getPrincipal();
        formatDates(tournamentDto);
        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.createTournament(executionId, user.getId(), tournamentDto);
    }

    @DeleteMapping("/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity deleteTournament(@PathVariable Integer tournamentId, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        tournamentService.deleteTournament(tournamentId, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/executions/{executionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> listOpenTournaments(@PathVariable int executionId) {
        return tournamentService.listOpenTournaments(executionId);
    }

    private void formatDates(TournamentDto tournament) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (tournament.getStartDate() != null && !tournament.getStartDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")){
            tournament.setStartDate(LocalDateTime.parse(tournament.getStartDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
        }
        if (tournament.getEndDate() !=null && !tournament.getEndDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})"))
            tournament.setEndDate(LocalDateTime.parse(tournament.getEndDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
    }

    @GetMapping("/tournaments/{tournamentId}/quiz")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public StatementQuizDto getTournamentQuiz(@PathVariable int tournamentId, Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.getTournamentQuiz(tournamentId, user.getId());
    }



}
