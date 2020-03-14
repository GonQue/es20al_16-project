package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationResponse;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class ClarificationResponseDto implements Serializable {

    private Integer id;
    private String teacherResponse;
    private String responseDate = null;

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClarificationResponseDto() {
    }

    public ClarificationResponseDto(ClarificationResponse clarificationResponse) {
        this.id = clarificationResponse.getId();
        this.teacherResponse = clarificationResponse.getTeacherResponse();

        if (clarificationResponse.getResponseDate() != null)
            this.responseDate = clarificationResponse.getResponseDate().format(formatter);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        return "ClarificationResponse{" +
                "id=" + id +
                ", response=" + teacherResponse +
                ", date=" + responseDate +
                '}';
    }
}
