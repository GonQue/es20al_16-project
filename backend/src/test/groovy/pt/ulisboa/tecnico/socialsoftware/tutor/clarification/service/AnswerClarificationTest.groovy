package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class AnswerClarificationTest extends Specification{

    def clarification

    def setup() {
        clarification = new QuestionClarification()
    }

    def 'the teacher clarifies the student'() {
        // the teacher clarifies the student
        expect: false
    }

    def 'the teacher with the id does not exist'() {
        // an exception is thrown
        expect: false
    }

    def 'the clarification already exists'() {
        // an exception is thrown
        expect: false
    }

    def 'status is NOT_ANSWERED'() {
        // an exception is thrown
        expect: false
    }

    def 'status is null'() {
        // an exception is thrown
        expect: false
    }

    def 'status is blank'() {
        // an exception is thrown
        expect: false
    }

    def 'teacher id is null'() {
        // an exception is thrown
        expect: false
    }

    def 'teacher id is blank'() {
        // an exception is thrown
        expect: false
    }

    def 'teacher response is null'() {
        // an exception is thrown
        expect: false
    }

    def 'teacher response is blank'() {
        // an exception is thrown
        expect: false
    }

}
