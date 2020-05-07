package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class TurnAvailableProposedQuestionTest extends Specification {

    @Autowired
    QuestionService questionService

    @Autowired
    ProposedQuestionService proposedQuestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    ProposedQuestionRepository proposedQuestionRepository

    @Autowired
    QuestionRepository  questionRepository

    @Autowired
    UserRepository userRepository

    def question
    def proposedQuestion

    def setup() {
        def teacher = new User("teacher", "teacher", 1, User.Role.TEACHER)
        userRepository.save(teacher)

        def student = new User("student", "student", 2, User.Role.STUDENT)
        userRepository.save(student)

        def course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        course.addCourseExecution(courseExecution)

        Set<CourseExecution> courseExecSet = new HashSet<>()
        courseExecSet.add(courseExecution)
        teacher.setCourseExecutions(courseExecSet)

        def questionDto = new QuestionDto()
        questionDto.setTitle("QUESTION_TITLE")
        questionDto.setContent("QUESTION_CONTENT")
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent("OPTION_CONTENT")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        question = new Question(course, questionDto)
        questionRepository.save(question)

        proposedQuestion = new ProposedQuestion()
        proposedQuestion.setStudent(student)
        proposedQuestion.setQuestion(question)
        proposedQuestion.setTeacher(teacher)
        proposedQuestionRepository.save(proposedQuestion)
    }


    def 'turn an approved proposed question available'(){
        given: "an approved question"
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED)
        def approvedQuestionDto = new ProposedQuestionDto(proposedQuestion)

        when:
        def result = proposedQuestionService.turnAvailable(proposedQuestion.getId(), approvedQuestionDto)

        then:
        result.getEvaluation() == ProposedQuestion.Evaluation.AVAILABLE.name()
        result.getQuestion().getStatus() == Question.Status.AVAILABLE.name()
    }


    def 'turn a rejected proposed question available'(){
        given: "a rejected question"
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.REJECTED)
        def rejectedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        rejectedQuestionDto.setJustification("JUSTIFICATION")

        when:
        proposedQuestionService.turnAvailable(proposedQuestion.getId(), rejectedQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == PROPQUESTION_NOT_APPROVED
    }


    def 'turn an awaiting proposed question available'(){
        given: "an awaiting question"
        def awaitingQuestionDto = new ProposedQuestionDto(proposedQuestion)

        when:
        proposedQuestionService.turnAvailable(proposedQuestion.getId(), awaitingQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == PROPQUESTION_NOT_APPROVED
    }


    def 'update and turn available a proposed question'() {
        given: "a changed question"
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED)
        question.setTitle("NEW_QUESTION_TITLE")
        question.setContent("NEW_QUESTION_CONTENT")
        question.setStatus(Question.Status.SUBMITTED)
        question.getOptions().get(0).setContent("NEW_OPTION_CONTENT")
        def proposedQuestionDto = new ProposedQuestionDto(proposedQuestion)

        when:
        def result = proposedQuestionService.turnAvailable(proposedQuestion.getId(), proposedQuestionDto)

        then:
        result.getId() == proposedQuestionDto.getId()
        result.getEvaluation() == ProposedQuestion.Evaluation.AVAILABLE.name()
        result.getQuestion().getId() == proposedQuestionDto.getQuestion().getId()
        result.getQuestion().getStatus() == Question.Status.AVAILABLE.name()
        result.getQuestion().getTitle()  == "NEW_QUESTION_TITLE"
        result.getQuestion().getContent() == "NEW_QUESTION_CONTENT"
        result.getQuestion().getOptions().size() == 1
        result.getQuestion().getOptions().get(0).getContent() == "NEW_OPTION_CONTENT"
    }


    @TestConfiguration
    static class TurnAvailableTestContextConfiguration {
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        ProposedQuestionService proposedQuestionService() {
            return new ProposedQuestionService()
        }
    }
}