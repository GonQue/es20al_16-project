package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.QuestionClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;

@Entity
@Table(name = "clarifications")
public class QuestionClarification {
    public enum Status {
        ANSWERED, NOT_ANSWERED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    private Integer questionId;
    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = null;
    private Integer teacherId;
    private String teacherResponse;

    @Column(name = "responseDate_date")
    private LocalDateTime responseDate = null;

    public QuestionClarification() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherResponse() {
        return teacherResponse;
    }

    public void setTeacherResponse(String teacherResponse) {
        this.teacherResponse = teacherResponse;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public void answerQuestionClarification(QuestionClarificationDto questionClarificationDto) {
        if(getStatus() == Status.ANSWERED || questionClarificationDto.getTeacherId() == null || questionClarificationDto.getTeacherResponse() == null)
            throw new TutorException(USER_NOT_FOUND, questionClarificationDto.getTeacherId());
        setStatus(Status.ANSWERED);
        setTeacherId(questionClarificationDto.getTeacherId());
        setTeacherResponse(questionClarificationDto.getTeacherResponse());
        // response date
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                "questionId" + questionId +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
