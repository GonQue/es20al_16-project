package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class QuestionClarificationDto implements Serializable {

    private Integer id;
    private Integer key;
    private Integer questionId;
    private Integer userId;
    private Integer answerId;
    private String content;
    private String status;
    private String creationDate = null;
    private Integer teacherId;
    private String teacherResponse;
    private String responseDate = null;

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public QuestionClarificationDto() {
    }

    public QuestionClarificationDto(QuestionClarification clarification) {
        this.id = clarification.getId();
        this.key = clarification.getKey();
        this.questionId = clarification.getQuestionId();
        this.content = clarification.getContent();
        this.status = clarification.getStatus();
        this.answerId = clarification.getAnswerId();
        this.teacherId = clarification.getTeacherId();
        this.userId = clarification.getUserId();
        this.teacherResponse = clarification.getTeacherResponse();

        if (clarification.getCreationDate() != null)
            this.creationDate = clarification.getCreationDate().format(formatter);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getResponseDate() { return responseDate; }

    public void setResponseDate(String responseDate) { this.responseDate = responseDate; }

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