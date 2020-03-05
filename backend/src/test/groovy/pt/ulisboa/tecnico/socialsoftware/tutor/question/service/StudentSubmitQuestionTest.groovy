package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class StudentSubmitQuestionTest extends Specification {
    static final String QUESTION_TITLE = "Question title"
    static final String QUESTION_CONTENT = "Question content"
    static final String OPTION_CONTENT = "Option content"
    static final String TOPIC_NAME = "Topic name"

    def questionPropService
    def user
    def propQuestionDto

    def setup() {
        questionPropService = new QuestionProposalService()
    }

    def 'the user is not a Student'() {
        given: "a teacher"
        user = new User()
        user.setKey(1)
        user.setRole(User.Role.TEACHER)
        and: "a proposedQuestionDto"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setUserKey(1)

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }

    // Existe um TutorPermissionEvaluator que talvez nao permita isso
    /*def 'the student is not enrolled in the course'() {
        given: "a course execution"
        courseExecution = new CourseExecution()
        user.setRole(User.Role.STUDENT)
        courseExecution.addUser(user)
        user.addCourse(courseExecution)
        and: "another student"
        def anotherUser = new User("name", "username2", 2, User.Role.STUDENT)

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }*/

    def 'the topic for the new question exists'() {
        given: "a topic"
        def topicDto = new TopicDto()
        topicDto.setId(1)
        topicDto.setName(TOPIC_NAME)
        and: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setKey(1)
        def topics = new ArrayList<TopicDto>()
        topics.add(topicDto)
        propQuestionDto.setTopics(topics)

        when:
        def result = questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        result.getKey() == 1
        result.getTopics().size() == 1
        result.getTopics().get(0).getId() == 1
        result.getTopics().get(0).getName() == TOPIC_NAME
    }

    def 'create a question with no image and 2 options'() {
        given: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setKey(1)
        propQuestionDto.setTitle(QUESTION_TITLE)
        propQuestionDto.setContent(QUESTION_CONTENT)
        and: "two options"
        def optionsDto = new OptionDto()
        optionsDto.setContent(OPTION_CONTENT)
        optionsDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionsDto)
        optionsDto = new OptionDto()
        optionsDto.setContent(OPTION_CONTENT)
        optionsDto.setCorrect(true)
        options.add(optionsDto)
        propQuestionDto.setOptions(options)
        and: "a user"
        user = new User("nome", "username", 1, User.Role.STUDENT)
        propQuestionDto.setUserKey(1)

        when:
        def result = questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage().getId() == null
        result.getOptions().size() == 2
        result.getUserKey() == 1
    }

    def 'create a question with no options'() {
        given: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setKey(1)
        propQuestionDto.setTitle(QUESTION_TITLE)
        propQuestionDto.setContent(QUESTION_CONTENT)
        and: "no options"
        propQuestionDto.setOptions(null)

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }

    def 'question title is empty'() {
        given: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setKey(1)
        propQuestionDto.setTitle(null)

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }

    def 'question title is blank'() {
        given: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setKey(1)
        propQuestionDto.setTitle("    ")

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }

    def 'question content is empty'() {
        given: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setKey(1)
        propQuestionDto.setContent(null)

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }

    def 'question content is blank'() {
        given: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setKey(1)
        propQuestionDto.setContent("     ")

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }
}
