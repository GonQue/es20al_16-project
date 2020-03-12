package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

public class ProposedQuestionDto {
    private Integer id;
    private UserDto student;
    private UserDto teacher;
    private String justification;
    private String evaluation;

    public ProposedQuestionDto() {
    }

    public ProposedQuestionDto(ProposedQuestion proposedQuestion) {
        this.id = proposedQuestion.getId();
        if (proposedQuestion.getTeacher() != null) {
            this.teacher = new UserDto(proposedQuestion.getTeacher());
            this.justification = proposedQuestion.getJustification();
            this.evaluation = proposedQuestion.getEvaluation().name();
        }
    }

    public Integer getId() { return id; }

    public UserDto getStudent() {
        return student;
    }

    public void setStudent(UserDto student) {
        this.student = student;
    }

    public UserDto getTeacher() {
        return teacher;
    }

    public void setTeacher(UserDto teacher) {
        this.teacher = teacher;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }
}
