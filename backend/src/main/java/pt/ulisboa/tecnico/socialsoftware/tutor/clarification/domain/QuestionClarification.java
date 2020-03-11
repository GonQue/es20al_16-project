package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import java.time.LocalDateTime;

public class QuestionClarification {
    public enum Status {
        ANSWERED, NOT_ANSWERED
    }

    private Integer id;
    private Integer key;
    private Integer questionId;
    private Integer answerId;
    private String content;
    private String status;
    private LocalDateTime creationDate = null;
    private Integer teacherId;
    private Integer userId;
    private String teacherResponse;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
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

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
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