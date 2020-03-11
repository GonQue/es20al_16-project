package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class OptionDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private boolean correct;
    private String content;
    private Set<QuestionAnswer> questionAnswers = new HashSet<>();

    public OptionDto() {
    }

    public OptionDto(Option option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.content = option.getContent();
        this.correct = option.getCorrect();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean getCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void addQuestionAnswer(QuestionAnswer questionAnswer) {
        questionAnswers.add(questionAnswer);
    }

    @Override
    public String toString() {
        return "OptionDto{" +
                "id=" + id +
                ", id=" + id +
                ", correct=" + correct +
                ", content='" + content + '\'' +
                '}';
    }
}