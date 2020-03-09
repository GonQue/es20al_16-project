package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class QuestionClarificationDto implements Serializable {

    private Integer id;
    private Integer key;
    private Integer questionId;
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

    public QuestionClarificationDto(QuestionClarification questionClarification) {
        this.id = questionClarification.getId();
        this.key = questionClarification.getKey();
        this.questionId = questionClarification.getQuestionId();
        this.content = questionClarification.getContent();
        this.status = questionClarification.getStatus().name();
        this.teacherId = questionClarification.getTeacherId();
        this.teacherResponse = questionClarification.getTeacherResponse();

        if (questionClarification.getCreationDate() != null)
            this.creationDate = questionClarification.getCreationDate().format(formatter);
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
