package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetProposedQuestionTest extends Specification {

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

    @Autowired
    TopicRepository topicRepository


    def "the student does not exist"(){
        when:
        proposedQuestionService.getProposedQuestions(100000)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }


    def "the proposed question exists"() {
        given: "a student"
        def student = new User("student", "student", 1, User.Role.STUDENT)
        userRepository.save(student)

        and: "a teacher"
        def teacher = new User("teacher", "teacher", 2, User.Role.TEACHER)
        userRepository.save(teacher)

        and: "a course"
        def course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)

        Set<CourseExecution> courseExecSet = new HashSet<>()
        courseExecSet.add(courseExecution)

        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        teacher.setCourseExecutions(courseExecSet)
        courseExecutionRepository.save(courseExecution)

        and: "an awaiting proposed question"

        def question = createQuestion(course, 1)

        def propQuestion = new ProposedQuestion()
        propQuestion.setQuestion(question)
        propQuestion.setStudent(student)
        propQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING)
        proposedQuestionRepository.save(propQuestion)

        student.addProposedQuestion(propQuestion)

        and: "an approved proposed question"

        def question2 = createQuestion(course, 2)

        def propQuestion2 = new ProposedQuestion()
        propQuestion2.setQuestion(question2)
        propQuestion2.setStudent(student)
        propQuestion2.setTeacher(teacher)
        propQuestion2.setEvaluation(ProposedQuestion.Evaluation.APPROVED)
        propQuestion2.setJustification(" ")
        proposedQuestionRepository.save(propQuestion2)

        student.addProposedQuestion(propQuestion2)

        and: "a rejected proposed question"
        def question3 = createQuestion(course, 3)

        def propQuestion3 = new ProposedQuestion()
        propQuestion3.setQuestion(question3)
        propQuestion3.setStudent(student)
        propQuestion3.setTeacher(teacher)
        propQuestion3.setEvaluation(ProposedQuestion.Evaluation.REJECTED)
        propQuestion3.setJustification("JUSTIFICATION")
        proposedQuestionRepository.save(propQuestion3)

        student.addProposedQuestion(propQuestion3)


        when:
        def result = proposedQuestionService.getProposedQuestions(student.getId())

        then: "the returned data are correct"
        result.size() == 3
        def awaitingProposedQuestion = result.get(2)
        awaitingProposedQuestion.studentId == student.getId()
        awaitingProposedQuestion.question.getKey() == 1
        awaitingProposedQuestion.evaluation == ProposedQuestion.Evaluation.AWAITING.name()

        def approvedProposedQuestion = result.get(1)
        approvedProposedQuestion.studentId == student.getId()
        approvedProposedQuestion.teacherId == teacher.getId()
        approvedProposedQuestion.question.getKey() == 2
        approvedProposedQuestion.evaluation == ProposedQuestion.Evaluation.APPROVED.name()
        approvedProposedQuestion.justification == " "

        def rejectedProposedQuestion = result.get(0)
        rejectedProposedQuestion.studentId == student.getId()
        rejectedProposedQuestion.teacherId == teacher.getId()
        rejectedProposedQuestion.question.getKey() == 3
        rejectedProposedQuestion.evaluation == ProposedQuestion.Evaluation.REJECTED.name()
        rejectedProposedQuestion.justification == "JUSTIFICATION"
    }


    def createQuestion(Course course, int key){
        def questionDto = new QuestionDto()
        questionDto.setKey(key)
        questionDto.setTitle("QUESTION_TITLE")
        questionDto.setContent("QUESTION_CONTENT")
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent("OPTION_CONTENT")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        def question = new Question(course, questionDto)
        questionRepository.save(question)

        return question
    }


    @TestConfiguration
    static class GetProposedQuestionTestContextConfiguration {

        @Bean
        ProposedQuestionService proposedQuestionService() {
            return new ProposedQuestionService()
        }
    }

}