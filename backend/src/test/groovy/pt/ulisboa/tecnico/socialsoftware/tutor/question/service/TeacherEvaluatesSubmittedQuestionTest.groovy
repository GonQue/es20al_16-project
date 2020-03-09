package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

class TeacherEvaluatesSubmittedQuestionTest extends Specification {

    def evaluation, teacher, teacherDto

    def setup() {
        evaluation = new QuestionProposalService()
        teacher = new User("name", "username", 1, User.Role.TEACHER)
        teacher.setId(1)
        teacherDto = new UserDto(teacher)
    }


    def 'the user is not a teacher'() {
        given: "a admin"
        def user = new User("name", "username", 1, User.Role.ADMIN)
        user.setId(1)
        def userDto = new UserDto(user)

        and: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto()
        proposedQuestion.setTeacher(userDto)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())
        proposedQuestion.setJustification(null)

        when:
        evaluation.teacherEvaluatesSubmittedQuestion(proposedQuestion)

        then:
        thrown(TutorException)
    }


    def 'the teacher teaches the course'() {
        given: "a course"
        def course = new CourseExecution()
        course.setId(1)
        course.setAcronym("COURSEACRONYM")
        Set<CourseExecution> courseset = new HashSet<>()
        courseset.add(course)
        teacher.setCourseExecutions(courseset)
        and: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto()
        proposedQuestion.setTeacher(teacherDto)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())

        when:
        def result = evaluation.teacherEvaluatesSubmittedQuestion(proposedQuestion)

        then:
        result.getTeacher().getUsername() == teacher.getUsername()
        teacher.getCourseExecutions().size() == 1
        teacher.getCourseExecutions().contains(course) == true
        teacher.hasEnrolledCourseAcronym(course.getAcronym()) == true
    }


    def 'the justification is empty'() {
        given: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto()
        proposedQuestion.setTeacher(teacherDto)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())

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
        proposedQuestion.setTeacher(teacherDto)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED.name())
        proposedQuestion.setJustification(null)

        when:
        def result = evaluation.teacherEvaluatesSubmittedQuestion(proposedQuestion)

        then:
        result.getEvaluation() == ProposedQuestion.Evaluation.APPROVED.name()
    }


    def 'the question is rejected and the justification is blank' (){
        given: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto()
        proposedQuestion.setTeacher(teacherDto)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.REJECTED.name())
        proposedQuestion.setJustification(" ")

        when:
        evaluation.teacherEvaluatesSubmittedQuestion(proposedQuestion)

        then:
        thrown(TutorException)
    }

}