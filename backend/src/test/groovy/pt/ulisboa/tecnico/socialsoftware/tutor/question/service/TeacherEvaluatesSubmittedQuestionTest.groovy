package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class TeacherEvaluatesSubmittedQuestionTest extends Specification {

    @Autowired
    QuestionProposalService questionProposalService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    ProposedQuestionRepository proposedQuestionRepository

    @Autowired
    UserRepository userRepository

    def teacher
    def teacherDto
    def course
    def courseExecution
    def pq

    def setup() {
        teacher = new User("name", "username", 1, User.Role.TEACHER)
        userRepository.save(teacher)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        course.addCourseExecution(courseExecution)

        Set<CourseExecution> courseExecSet = new HashSet<>()
        courseExecSet.add(courseExecution)
        teacher.setCourseExecutions(courseExecSet)

        teacherDto = new UserDto(teacher)

        pq = new ProposedQuestion()
        proposedQuestionRepository.save(pq)

    }


    def 'the user is not a teacher'() {
        given: "a admin"
        def user = new User("name2", "username2", 2, User.Role.ADMIN)
        userRepository.save(user)
        def userDto = new UserDto(user)

        and: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto(pq)
        proposedQuestion.setTeacher(userDto)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())
        proposedQuestion.setJustification(" ")

        when:
        questionProposalService.teacherEvaluatesSubmittedQuestion(course.getId(), proposedQuestion)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ACCESS_DENIED
    }


    def 'the teacher is empty'(){
        given: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto(pq)
        proposedQuestion.setTeacher(null)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())

        when:
        questionProposalService.teacherEvaluatesSubmittedQuestion(course.getId(), proposedQuestion)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_IS_EMPTY
    }


    def 'the teacher teaches the course'() {
        given: "a course"
        def course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        and: "a course execution"
        def courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)

        Set<CourseExecution> courseExecSet = new HashSet<>()
        courseExecSet.add(courseExecution)
        teacher.setCourseExecutions(courseExecSet)

        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)

        and: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto(pq)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())
        proposedQuestion.setJustification("JUSTIFICATION")
        proposedQuestion.setTeacher(teacherDto)

        when:
        def result = questionProposalService.teacherEvaluatesSubmittedQuestion(course.getId(), proposedQuestion)

        then:
        result.getTeacher().getUsername() == teacher.getUsername()
        teacher.getCourseExecutions().size() == 1
        teacher.getCourseExecutions().contains(courseExecution) == true
    }


    def 'the justification is empty'() {
        given: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto(pq)
        proposedQuestion.setTeacher(teacherDto)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())

        and: "empty justification"
        proposedQuestion.setJustification(null)

        when:
        questionProposalService.teacherEvaluatesSubmittedQuestion(course.getId(), proposedQuestion)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.JUSTIFICATION_IS_EMPTY
    }


    def 'the question is approved'() {
        given: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto(pq)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED.name())
        proposedQuestion.setJustification(" ")
        proposedQuestion.setTeacher(teacherDto)

        when:
        def result = questionProposalService.teacherEvaluatesSubmittedQuestion(course.getId(), proposedQuestion)

        then:
        result.getEvaluation() == ProposedQuestion.Evaluation.APPROVED.name()
    }


    def 'the question is rejected and the justification is blank' (){
        given: "a proposed question"
        def proposedQuestion = new ProposedQuestionDto(pq)
        proposedQuestion.setTeacher(teacherDto)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.REJECTED.name())
        proposedQuestion.setJustification(" ")

        when:
        questionProposalService.teacherEvaluatesSubmittedQuestion(course.getId(), proposedQuestion)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.JUSTIFICATION_IS_BLANK
    }

    def 'the proposed question was already approved' (){
        given: "a question"
        pq.setTeacher(teacher)
        pq.setEvaluation(ProposedQuestion.Evaluation.APPROVED)
        def proposedQuestion = new ProposedQuestionDto(pq)
        proposedQuestion.setJustification(" ")

        and: "a repeated evaluation"
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED.name())
        proposedQuestion.setTeacher(teacherDto)

        when:
        questionProposalService.teacherEvaluatesSubmittedQuestion(course.getId(), proposedQuestion)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.PQ_ALREADY_EVALUATED
    }

    @TestConfiguration
    static class TeacherEvaluateTestContextConfiguration {

        @Bean
        QuestionProposalService questionProposalService() {
            return new QuestionProposalService()
        }
    }

}