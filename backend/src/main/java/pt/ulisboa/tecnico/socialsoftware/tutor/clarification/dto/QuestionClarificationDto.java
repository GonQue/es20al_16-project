package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class QuestionClarificationDto implements Serializable {

    private Integer id;
    private String content;
    private String status;
    private String creationDate = null;

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public QuestionClarificationDto() {
    }

    public QuestionClarificationDto(QuestionClarification questionClarification) {
        this.id = questionClarification.getId();
        this.content = questionClarification.getContent();
        this.status = questionClarification.getStatus().name();

        if (questionClarification.getCreationDate() != null)
            this.creationDate = questionClarification.getCreationDate().format(formatter);
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
        return "QuestionDto{" +
                "id=" + id +
                ", content='" + content +
                ", status='" + status +
                ", creation date='" + creationDate +
                '}';
    }
}
