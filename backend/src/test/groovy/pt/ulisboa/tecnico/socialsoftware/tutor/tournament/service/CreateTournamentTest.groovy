package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

class CreateTournamentTest extends Specification {

    def tournamentService

    def setup(){
        tournamentService = new TournamentService();
    }

    def "create a tournament with 2 topics"(){
        // create a tournament with a name, creator, start and end time, 2 topics, number of questions,
        expect: false
    }

    def "tournament creator is not a student"(){
        //throw exception
        expect: false
    }

    def "End time before start time"(){
       //throw exception
        expect: false
    }

    def "create tournament with 0 questions"(){
        //throw exception
        expect: false
    }

    def "create tournament with null topics"(){
        //throw exception
        expect: false
    }
    
    def "tournament name is empty"(){
        //throw exception
        expect: false
    } 
    
    def "tournament name is blank"(){
        //throw exception
        expect: false
    }

}
