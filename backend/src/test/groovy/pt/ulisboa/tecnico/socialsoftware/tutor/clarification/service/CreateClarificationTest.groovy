package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import spock.lang.Specification

class CreateClarificationTest extends Specification{

    def clarification
    def question

    def setup() {
        clarification = new QuestionClarification()
        question = new Question()
    }

    def 'create a clarification request'(){
        // the clarification is created
        expect: false
    }

    def 'create a clarification request with non-existing question'(){
        // an exception is thrown
        expect: false
    }

    def 'create a clarification request for a question not answered by user'(){
        // an exception is thrown
        expect: false
    }

    def 'create two identical clarification requests'(){
        // an exception is thrown
        expect: false
    }

    def 'the user is not a student'(){
        // an exception is thrown
        expect: false
    }

    def 'the clarification content is null'(){
        // an exception is thrown
        expect: false
    }

    def 'the clarification content is blank'(){
        // an exception is thrown
        expect: false
    }
    
    def 'the clarification userId is null'(){
        // an exception is thrown
        expect: false
    }

    def 'the clarification userId is blank'(){
        // an exception is thrown
        expect: false
    }
}
