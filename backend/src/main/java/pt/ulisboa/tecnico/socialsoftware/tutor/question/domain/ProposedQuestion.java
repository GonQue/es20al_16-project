package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;

public class ProposedQuestion extends Question {

    public enum Evaluation {
        APPROVED, REJECTED, AWAITING
    }

    private Integer userKey;
    private String justification;
    private Evaluation evaluation = Evaluation.AWAITING;
    private Integer assignedTeacherID; // maybe its needed

    public ProposedQuestion(Course course, ProposedQuestionDto proposedQuestionDto) {
        super(course, proposedQuestionDto);
    }
}
