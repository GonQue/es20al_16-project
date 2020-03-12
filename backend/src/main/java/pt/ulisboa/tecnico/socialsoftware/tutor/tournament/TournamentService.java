package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {
   @Autowired
   private CourseExecutionRepository courseExecutionRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private TopicRepository topicRepository;

   @Autowired
   private QuizRepository quizRepository;

   @PersistenceContext
   EntityManager entityManager;

   @Transactional(isolation = Isolation.REPEATABLE_READ)
   public TournamentDto createTournament(int executionId, TournamentDto tournamentDto){

      CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
      User creatorUser = userRepository.findById(tournamentDto.getCreator().getId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, tournamentDto.getCreator().getId()));
      Quiz quiz = quizRepository.findById(tournamentDto.getQuiz().getId()).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, tournamentDto.getQuiz().getId()));

      if(creatorUser.getRole() != User.Role.STUDENT){
         throw new TutorException(TOURNAMENT_CREATOR_NOT_STUDENT);
      }

      if (!creatorUser.getCourseExecutions().stream()
              .anyMatch(ce -> ce.getId().equals(courseExecution.getId()))) {
         throw new TutorException(TOURNAMENT_CREATOR_NOT_ENROLLED);
      }

      if (!creatorUser.getCourseExecutions().stream()
              .anyMatch(ce -> ce.getId().equals(courseExecution.getId()))) {
         throw new TutorException(TOURNAMENT_CREATOR_NOT_ENROLLED);
      }


      if(tournamentDto.getTopics() == null){
         tournamentDto.setTopics(new ArrayList<>(Arrays.asList()));
      }

      Set<Topic> topics = new HashSet<>();
      List<TopicDto> topicsDto = tournamentDto.getTopics();
      for(TopicDto topicDto: topicsDto){
         Topic topic = topicRepository.findById(topicDto.getId()).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicDto.getId()));
         topics.add(topic);
      }

      Tournament tournament = new Tournament(creatorUser, courseExecution, quiz, tournamentDto);
      tournament.setTopics(topics);

      entityManager.persist(tournament);

      return new TournamentDto(tournamentDto.getQuiz(), topicsDto, tournament);

   }
}
