package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import spock.lang.Specification

class TeacherEvaluatesSubmittedQuestionTest extends Specification {

    def evaluation

    def setup() {
        evaluation = new QuestionProposalService()
    }

    def 'the teacher teaches the course'() {
        // the evaluation is created
        expect: false
    }

    def 'the justification is empty'() {
        // an exception is thrown
        expect: false
    }

    def 'the justification is blank'() {
        // the evaluation is created
        expect: false
    }

    def 'the question is approved'() {
        // the evaluation is created
        expect: false
    }

    def 'the question is rejected and the justification is blank' (){
        // the evaluation is created
        expect: false
    }

}
