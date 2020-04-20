package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOPIC_NOT_FOUND;

public class TournamentDto implements Serializable {
    private Integer id;
    private String name;
    private UserDto creator;
    private String startDate;
    private String endDate;
    private int numberOfQuestions;
    private List<TopicDto> topics = new ArrayList<>();
    private List<String> enrolled = new ArrayList<>();

    private QuizDto quiz;
    private String status;

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public TournamentDto(){}

    public TournamentDto(UserDto userDto, QuizDto quizDto,List<TopicDto> topics,Tournament tournament){
        this.id = tournament.getId();
        this.name = tournament.getName();
        this.creator = userDto;
        this.startDate = tournament.getStartDate().format(formatter);
        this.endDate = tournament.getEndDate().format(formatter);
        this.numberOfQuestions = tournament.getNumberOfQuestions();
        this.status = tournament.getStatus().name();
        this.quiz = quizDto;
        this.topics = topics;
        this.enrolled = new ArrayList<>();
    }

    public TournamentDto(Tournament tournament){
        this.id = tournament.getId();
        this.name = tournament.getName();
        this.creator = new UserDto(tournament.getCreator());
        this.startDate = tournament.getStartDate().format(formatter);
        this.endDate = tournament.getEndDate().format(formatter);
        this.numberOfQuestions = tournament.getNumberOfQuestions();
        this.status = tournament.getStatus().name();
        if(quiz!=null) {
            this.quiz = new QuizDto(tournament.getQuiz(), false);
        }
        Set<Topic> topicsTournament = tournament.getTopics();
        for(Topic topic: topicsTournament){
            this.topics.add(new TopicDto(topic));
        }

        Set<User> enrolledTournament = tournament.getEnrolled();
        for(User user: enrolledTournament){
            this.enrolled.add(user.getUsername());
        }

    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
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

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public List<String> getEnrolled() {return enrolled;}

    public void setEnrolled(List<String> enrolled) {this.enrolled = enrolled;    }

}
