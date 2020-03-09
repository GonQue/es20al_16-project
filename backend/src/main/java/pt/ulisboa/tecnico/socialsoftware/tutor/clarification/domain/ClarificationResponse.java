package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clarification_responses")
public class ClarificationResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "clarification_id")
    private QuestionClarification clarification;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;
    private String teacherResponse;

    @Column(name = "responseDate_date")
    private LocalDateTime responseDate = null;

    public ClarificationResponse() {
    }

    public ClarificationResponse(QuestionClarification c, User t, ClarificationResponseDto questionClarificationDto) {
        clarification = c;
        teacher = t;
        teacherResponse = questionClarificationDto.getTeacherResponse();
        responseDate = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuestionClarification getClarification() { return clarification; }

    public void setClarification(QuestionClarification clarification) { this.clarification = clarification; }

    public User getTeacher() { return teacher; }

    public void setTeacher(User teacher) { this.teacher = teacher; }

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
        return "ClarificationResponse{" +
                "id=" + id +
                ", clarification='" + clarification +
                ", teacher='" + teacher +
                ", teacher response='" + teacherResponse +
                ", response date='" + responseDate +
                '}';
    }
}
