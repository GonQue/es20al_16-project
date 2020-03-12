package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

public class ProposedQuestionDto {

    private Integer id;
    private QuestionDto question;
    private UserDto student;

    public ProposedQuestionDto() {}

    public ProposedQuestionDto(ProposedQuestion proposedQuestion) {
        this.id = proposedQuestion.getId();
        this.question = new QuestionDto(proposedQuestion.getQuestion());
        this.student = new UserDto(proposedQuestion.getStudent());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public UserDto getStudent() {
        return student;
    }

    public void setStudent(UserDto student) {
        this.student = student;
    }
}
