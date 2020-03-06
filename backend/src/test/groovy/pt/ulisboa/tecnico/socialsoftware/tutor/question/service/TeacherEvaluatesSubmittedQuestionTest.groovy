package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class TeacherEvaluatesSubmittedQuestionTest extends Specification {

    def evaluation

    def setup() {
        evaluation = new QuestionProposalService()
    }

    def 'the user is not a teacher'() {
        given: "a admin"
        def user = new User()
        user.setKey(1)
        user.setRole(User.Role.ADMIN)
        and: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto()
        proposedQuestion.setUserKey(1)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())
        proposedQuestion.setJustification(null)
        proposedQuestion.setAssignedTeacherID(1)

        when:
        evaluation.teacherEvaluatesSubmittedQuestion(proposedQuestion)

        then:
        thrown(TutorException)
    }

    def 'the teacher does not teaches the course'() {
        // an exception is thrown
        expect: false
    }

    def 'the justification is empty'() {
        given: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto()
        proposedQuestion.setUserKey(1)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())
        proposedQuestion.setAssignedTeacherID(1)
        and: "empty justification"
        proposedQuestion.setJustification(null)


        when:
        evaluation.teacherEvaluatesSubmittedQuestion(proposedQuestion)

        then:
        thrown(TutorException)
    }


    def 'the question is approved'() {
        given: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto()
        proposedQuestion.setUserKey(1)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED.name())
        proposedQuestion.setJustification(null)
        proposedQuestion.setAssignedTeacherID(1)

        when:
        def result = evaluation.teacherEvaluatesSubmittedQuestion(proposedQuestion)

        then:
        result.get
        result.getEvaluation() == ProposedQuestion.Evaluation.APPROVED.name()
    }

    def 'the question is rejected and the justification is blank' (){
        given: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto()
        proposedQuestion.setUserKey(1)
        proposedQuestion.setAssignedTeacherID(1)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.REJECTED.name())
        proposedQuestion.setJustification(" ")

        when:
        evaluation.teacherEvaluatesSubmittedQuestion(proposedQuestion)

        then:
        thrown(TutorException)
    }

}
