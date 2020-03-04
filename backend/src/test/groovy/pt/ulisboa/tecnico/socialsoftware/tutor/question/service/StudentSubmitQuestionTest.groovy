package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class StudentSubmitQuestionTest extends Specification {
    def user
    def proposedQuestion

    def setup() {
        proposedQuestion = new ProposedQuestion();
        user = new User()
        user.setKey(1)
    }

    def 'the user is not a Student'() {
        // an exception is thrown
        expect: false
    }

    def 'the topic for the new question exists'() {
        // the proposed question is created
        expect: false
    }

    def 'student creates a question with no image and 4 options'() {
        // the proposed question is created
        expect: false
    }

    def 'student creates a question with no options'() {
        // an exception is thrown
        expect: false
    }

    def 'question title is empty'() {
        // an exception is thrown
        expect: false
    }

    def 'question title is blank'() {
        // an exception is thrown
        expect: false
    }

    def 'question content is empty'() {
        // an exception is thrown
        expect: false
    }

    def 'question content is blank'() {
        // an exception is thrown
        expect: false
    }
}
