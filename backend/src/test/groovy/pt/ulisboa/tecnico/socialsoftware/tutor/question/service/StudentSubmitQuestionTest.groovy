package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.TutorApplication
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class StudentSubmitQuestionTest extends Specification {
    static final String QUESTION_TITLE = "Question title"
    static final String QUESTION_CONTENT = "Question content"
    static final String OPTION_CONTENT = "Option content"
    static final String TOPIC_NAME = "Topic name"
    static final String COURSE_NAME = "Course name"
    static final String COURSE_ACRONYM = "AC"
    static final String COURSE_ACADEMIC_TERM = "2S"

    @Autowired
    QuestionProposalService questionProposalService

    @Autowired
    QuestionService questionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    def course
    def student
    def studentDto
    def propQuestionDto
    def questionDto

    def setup() {
        student = new User("name", "username", 1, User.Role.STUDENT)
        userRepository.save(student)
        studentDto = new UserDto(student)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
    }

    def 'the user is not a Student'() {
        given: "a user"
        def user = new User("name", "username2", 2, User.Role.TEACHER)
        userRepository.save(user)
        and: "a userDto"
        def userDto = new UserDto(user)
        and: "a proposedQuestionDto"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(userDto)

        when:
        questionProposalService.studentSubmitQuestion(course.getId(), propQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ACCESS_DENIED
    }

    def 'the student is not enrolled in the course'() {
        given: "a course execution"
        def courseExecution = new CourseExecution(course, COURSE_ACRONYM, COURSE_ACADEMIC_TERM, Course.Type.TECNICO)
        //courseExecution.setId(1)
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        //courseExecutionRepository.save(courseExecution)
        and: "another student"
        def anotherStudent = new User("name", "username2", 2, User.Role.STUDENT)
        userRepository.save(anotherStudent)
        def anotherStudentDto = new UserDto(anotherStudent)
        and: "a proposedQuestionDto"
        propQuestionDto = new ProposedQuestionDto()
        //propQuestionDto.setKey(1)
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(anotherStudentDto)

        when:
        questionProposalService.studentSubmitQuestion(course.getId(), propQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_ENROLLED_COURSE
    }

   /*def 'the topic for the new question exists in the student course'() {
        given: "a topic"
        def topic = new Topic()
        topic.setId(1)
        topic.setName(TOPIC_NAME)
        and: "a course"
        def course = new Course("course", Course.Type.TECNICO)
        course.addTopic(topic)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, "AC", "1S", Course.Type.TECNICO)
        def courseSet = new HashSet<CourseExecution>()
        courseSet.add(courseExecution)
        student.setCourseExecutions(courseSet)
        and: "a topicDto"
        def topicDto = new TopicDto(topic)
        def topics = new ArrayList<TopicDto>()
        topics.add(topicDto)
        and: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setId(1)
        questionDto.setTopics(topics)
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(studentDto)

        when:
        def result = questionProposalService.studentSubmitQuestion(propQuestionDto)

        then:
        result.getKey() == 1
        result.getQuestion().getTopics().size() == 1
        result.getQuestion().getTopics().get(0).getId() == 1
        result.getQuestion().getTopics().get(0).getName() == TOPIC_NAME
        result.getStudent().getId() == student.getId()
        student.getCourseExecutions().size() == 1
        student.getCourseExecutions().get(0).getCourse().getTopics().size() == 1
        student.getCourseExecutions().get(0).getCourse().getTopics().getId() == 1
    }*/

    def 'the question is not empty'() {
        given: "a course execution"
        def courseExecution = new CourseExecution(course, COURSE_ACRONYM, COURSE_ACADEMIC_TERM, Course.Type.TECNICO)
        //courseExecution.setId(1)
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        courseExecutionRepository.save(courseExecution)
        //userRepository.save(student)
        and: "a proposed question"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setQuestion(questionDto)
        //propQuestionDto.setId(1)
        propQuestionDto.setStudent(studentDto)

        when:
        def result = questionProposalService.studentSubmitQuestion(course.getId(), propQuestionDto)

        then:
        result.getId() == 1
        result.getQuestion() != null
        result.getQuestion().getKey() == 1
    }

    def 'the student is empty'() {
        given: "a proposed question"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setQuestion(questionDto)
        //propQuestionDto.setId(1)
        propQuestionDto.setStudent(null)

        when:
        questionProposalService.studentSubmitQuestion(course.getId(), propQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_IS_EMPTY
    }

    @TestConfiguration
    static class PropQuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        QuestionProposalService questionPropService() {
            return new QuestionProposalService()
        }
    }
}
