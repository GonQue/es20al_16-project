package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;

public class ProposedQuestion extends Question {

    private Integer userID;
    private String justification;
    private Integer assignedTeacherID; // maybe its needed

    public ProposedQuestion() {
    }

    public ProposedQuestion(Course course, ProposedQuestionDto proposedQuestionDto) {
        super(course, proposedQuestionDto);
    }
}
