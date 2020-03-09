package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.QuestionClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.QuestionClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.QuestionClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class AnswerClarificationTest extends Specification{
    public static final String CLARIFICATION_CONTENT = 'question content'
    public static final String TEACHER_RESPONSE = "teacher response"

    @Autowired
    QuestionClarificationService questionClarificationService

    @Autowired
    QuestionClarificationRepository questionClarificationRepository


    def questionClarification
    def teacher

    def setup() {
        given: "create a teacher to answer the clarification"
        teacher = new User()
        teacher.setId(1)
        teacher.setRole(User.Role.TEACHER)
        and: "create a question clarification need"
        questionClarification = new QuestionClarification()
        questionClarification.setId(1)
        questionClarification.setStatus(QuestionClarification.Status.ANSWERED)
        questionClarification.setTeacherId(teacher.getId())
        questionClarification.setTeacherResponse(TEACHER_RESPONSE)
    }

    def 'the teacher clarifies the student'() {
        given: "create clarification need"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(question.getId())
        questionClarificationDto.setStatus(questionClarification.getStatus())
        questionClarificationDto.setTeacherId(questionClarification.getTeacherId())
        questionClarificationDto.setTeacherResponse(questionClarification.getTeacherResponse())

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then: "the need is clarified"
        questionClarificationRepository.count == 1L
        def result = questionClarificationRepository.findAll().get(0)
        result.getStatus() == QuestionClarification.Status.ANSWERED
        //questionClarification date
        result.getTeacherId() == questionClarification.getTeacherId()
        result.getTeacherResponse() == TEACHER_RESPONSE
        //response date
        and: "still not changed"
        result.getId() == questionClarification.getId()
        result.getKey() == questionClarification.getKey()
        result.getQuestionId() == questionClarification.getQuestionId()
        result.getContent() == CLARIFICATION_CONTENT
    }

    def 'the teacher with the id does not exist'() {
        given: "a questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(question.getId())
        questionClarificationDto.setStatus(questionClarification.getStatus())
        questionClarificationDto.setTeacherResponse(questionClarification.getTeacherResponse())
        and: "teacherId that not exists"
        questionClarificationDto.setTeacherId(0)

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the questionClarification does not exists'() {
        given: "a questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setStatus(questionClarification.getStatus())
        questionClarificationDto.setTeacherId(questionClarification.getTeacherId())
        questionClarificationDto.setTeacherResponse(questionClarification.getTeacherResponse())
        and: "questionClarificationId that not exists"
        questionClarificationDto.setId(0)

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'status is ANSWERED'() {
        given: "a questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(question.getId())
        questionClarificationDto.setTeacherId(questionClarification.getTeacherId())
        questionClarificationDto.setTeacherResponse(questionClarification.getTeacherResponse())
        and: "the question clarification already has been answered"
        questionClarificationDto.setStatus(Question.status.ANSWERED)

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'status is null'() {
        given: "a questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(question.getId())
        questionClarificationDto.setTeacherId(questionClarification.getTeacherId())
        questionClarificationDto.setTeacherResponse(questionClarification.getTeacherResponse())
        and: "status null"
        questionClarificationDto.setStatus(null)

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'status is blank'() {
        given: "a questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(question.getId())
        questionClarificationDto.setTeacherId(questionClarification.getTeacherId())
        questionClarificationDto.setTeacherResponse(questionClarification.getTeacherResponse())
        and: "status blank"
        questionClarificationDto.setStatus("   ")

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'teacher id is null'() {
        given: "a questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(question.getId())
        questionClarificationDto.setStatus(questionClarification.getStatus())
        questionClarificationDto.setTeacherResponse(questionClarification.getTeacherResponse())
        and: "teacherId null"
        questionClarificationDto.setTeacherId(null)

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'teacher id is blank'() {
        given: "a questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(question.getId())
        questionClarificationDto.setStatus(questionClarification.getStatus())
        questionClarificationDto.setTeacherId(questionClarification.getTeacherId())
        questionClarificationDto.setTeacherResponse(questionClarification.getTeacherResponse())
        and: "teacher id blank"
        questionClarificationDto.setTeacherId(0)

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'teacher response is null'() {
        given: "a questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(question.getId())
        questionClarificationDto.setStatus(questionClarification.getStatus())
        questionClarificationDto.setTeacherId(questionClarification.getTeacherId())
        and: "response null"
        questionClarificationDto.setTeacherResponse(null)

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'teacher response is blank'() {
        given: "a questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(question.getId())
        questionClarificationDto.setStatus(questionClarification.getStatus())
        questionClarificationDto.setTeacherId(questionClarification.getTeacherId())
        and: "response blank"
        questionClarificationDto.setTeacherResponse("   ")

        when:
        questionClarificationService.answerClarification(questionClarificationDto)

        then:
        thrown(TutorException)
    }
}
