package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

class GetOpenTournaments extends Specification{
    def tournService
    
    def setup(){
        tournService = new TournamentService();
    }
    
    def "no open tournaments"{
        //returns empty
    }

    def "one open tournaments"{
        //get tournament
    }

    def "twenty open tournaments"{
        //get tournaments
    }
    
    def "closed tournament on the list"{
        //throw exception
    }
}
