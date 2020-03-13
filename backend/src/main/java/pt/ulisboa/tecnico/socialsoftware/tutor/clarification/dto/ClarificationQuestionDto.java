package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class ClarificationQuestionDto implements Serializable {

    private Integer id;
    private String content;
    private String status;
    private String creationDate = null;

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClarificationQuestionDto() {
    }

    public ClarificationQuestionDto(ClarificationQuestion clarificationQuestion) {
        this.id = clarificationQuestion.getId();
        this.content = clarificationQuestion.getContent();
        this.status = ClarificationQuestion.Status.NOT_ANSWERED.name();

        if (clarificationQuestion.getCreationDate() != null)
            this.creationDate = clarificationQuestion.getCreationDate().format(formatter);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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