package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class ClarificationQuestionDto implements Serializable {

    private Integer id;
    private Integer answerId;
    private String questionContent;
    private String content;
    private String status;
    private Boolean needClarification;
    private String creationDate = null;

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClarificationQuestionDto() {
    }

    public ClarificationQuestionDto(ClarificationQuestion clarificationQuestion) {
        this.id = clarificationQuestion.getId();
        this.answerId = clarificationQuestion.getAnswer().getId();
        this.questionContent = clarificationQuestion.getQuestion().getContent();
        this.content = clarificationQuestion.getContent();
        this.status = clarificationQuestion.getStatus().name();
        this.needClarification = clarificationQuestion.getNeedClarification();

        if (clarificationQuestion.getCreationDate() != null)
            this.creationDate = clarificationQuestion.getCreationDate().format(formatter);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnswerId() { return answerId; }

    public void setAnswerId(Integer answerId) { this.answerId = answerId; }

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

    public Boolean getNeedClarification() { return needClarification; }

    public void setNeedClarification(Boolean needClarification) { this.needClarification = needClarification; }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getQuestionContent() { return questionContent; }

    public void setQuestionContent(String questionContent) { this.questionContent = questionContent; }

    @Override
    public String toString() {
        return "ClarificationQuestionDto{" +
                "id=" + id +
                ", content='" + content +
                ", status='" + status +
                ", creation date='" + creationDate +
                '}';
    }
}
