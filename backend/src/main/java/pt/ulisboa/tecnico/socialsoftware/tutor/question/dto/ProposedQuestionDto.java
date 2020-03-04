package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;

public class ProposedQuestionDto extends QuestionDto {
    private Integer userID;
    private String justification;
    private Integer assignedTeacherID; // maybe its needed

    public ProposedQuestionDto() {
    }

    public ProposedQuestionDto(ProposedQuestion proposedQuestion) {

    }
}
