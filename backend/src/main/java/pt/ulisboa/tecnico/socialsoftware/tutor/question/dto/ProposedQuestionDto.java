package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;

public class ProposedQuestionDto extends QuestionDto {
    private Integer userKey;
    private String justification;
    private Integer assignedTeacherID; // maybe its needed

    public ProposedQuestionDto() {
    }

    public ProposedQuestionDto(ProposedQuestion proposedQuestion) {
        super(proposedQuestion);
    }

    public void setUserKey(Integer key) { userKey = key; }

    public Integer getUserKey() { return userKey; }
}
