package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

public class TournamentDto implements Serializable {
    private Integer id;
    private Integer key;
    private String name;
    private UserDto creator;
    private String startDate;
    private String endDate;
    private int numberOfQuestions;
    private List<TopicDto> topics = new ArrayList<>();
    private QuizDto quiz;


    public TournamentDto(){}

    public TournamentDto(Tournament tournament){


    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCreator(UserDto creator) {
        this.creator = creator;
    }

    public UserDto getCreator() { return creator; }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() { return startDate; }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() { return endDate; }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getNumberOfQuestions() { return numberOfQuestions; }

    public void setTopics(List<TopicDto> newTopics){ this.topics = newTopics; }

    public List<TopicDto> getTopics(){ return topics;}

    public QuizDto getQuiz() { return quiz; }

    public void setQuiz(QuizDto quiz) { this.quiz = quiz; }
}
