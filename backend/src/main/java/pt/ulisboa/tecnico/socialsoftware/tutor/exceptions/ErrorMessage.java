package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {

    INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION("Invalid academic term for course execution"),
    INVALID_ACRONYM_FOR_COURSE_EXECUTION("Invalid acronym for course execution"),
    INVALID_CONTENT_FOR_OPTION("Invalid content for option"),
    INVALID_CONTENT_FOR_QUESTION("Invalid content for question"),
    INVALID_NAME_FOR_COURSE("Invalid name for course"),
    INVALID_NAME_FOR_TOPIC("Invalid name for topic"),
    INVALID_SEQUENCE_FOR_OPTION("Invalid sequence for option"),
    INVALID_SEQUENCE_FOR_QUESTION_ANSWER("Invalid sequence for question answer"),
    INVALID_TITLE_FOR_ASSESSMENT("Invalid title for assessment"),
    INVALID_TITLE_FOR_QUESTION("Invalid title for question"),
    INVALID_URL_FOR_IMAGE("Invalid url for image"),
    INVALID_TYPE_FOR_COURSE("Invalid type for course"),
    INVALID_TYPE_FOR_COURSE_EXECUTION("Invalid type for course execution"),
    INVALID_AVAILABLE_DATE_FOR_QUIZ("Invalid available date for quiz"),
    INVALID_CONCLUSION_DATE_FOR_QUIZ("Invalid conclusion date for quiz"),
    INVALID_RESULTS_DATE_FOR_QUIZ("Invalid results date for quiz"),
    INVALID_TITLE_FOR_QUIZ("Invalid title for quiz"),
    INVALID_TYPE_FOR_QUIZ("Invalid type for quiz"),
    INVALID_QUESTION_SEQUENCE_FOR_QUIZ("Invalid question sequence for quiz"),

    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    COURSE_NOT_FOUND("Course not found with name %s"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %d not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    USER_NOT_ENROLLED_COURSE("User is not enrolled in the question's course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    TOURNAMENT_CREATOR_NOT_STUDENT("The creator of the tournament is not a student"),
    TOURNAMENT_CREATOR_NOT_ENROLLED("The creator of the tournament is not enrolled in the course execution of the tournament"),
    TOURNAMENT_TIME_INVALID("The start and end time for the tournament are invalid"),
    TOURNAMENT_NAME_INVALID("Tournament name is null or empty"),
    TOURNAMENT_NUMBER_OF_QUESTIONS_INVALID("Number of questions for the tournament is invalid"),
    TOURNAMENT_NO_LONGER_AVAILABLE("Tournament already ended"),
    NOT_ENOUGH_PARTICIPANTS("Not enough participants in tournament"),

    TOURNAMENT_QUIZ_NOT_FOUND("Tournament cant be created without a quiz"),
    TOURNAMENT_NO_CREATOR("Tournament cant be created without a creator"),
    TOURNAMENT_ENROLLED_NOT_STUDENT("The user enrolled in the tournament is not a student"),
    TOURNAMENT_NOT_FOUND("The tournament does not exists %s"),
    USER_NOT_IN_COURSE_EXECUTION("The student is not enrolled in the course of the tournament he wants to enroll"),
    STUDENT_ALREADY_ENROLLED("The student tries to enroll in a tournament that he is already enrolled"),
    TOURNAMENT_CLOSED("Student cant enroll in a closed tournament"),
    TOURNAMENT_NO_STUDENT_TO_ENROLL("Missing student to enroll tournament"),
    INVALID_USERNAME("Username is null or empty"),
    TOURNAMENT_STARTED("the tournament already started"),
    TOURNAMENT_NOT_THE_CREATOR("is not the creator of the tournament"),

    NO_CORRECT_OPTION("Question does not have a correct option"),

    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    ONE_CORRECT_OPTION_NEEDED("Questions need to have 1 and only 1 correct option"),
    CANNOT_CHANGE_ANSWERED_QUESTION("Can not change answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),

    QUESTION_CLARIFICATION_NOT_FOUND("Question clarification not found with id %d"),
    QUESTION_CLARIFICATION_RESPONSE_NOT_FOUND("Clarification response not found with id %d"),
    RESPONSE_CONTENT("The response should have a valid content"),
    CLARIFICATION_CONTENT("The clarification should have a valid content"),
    USER_IS_NOT_A_TEACHER("The user is not a teacher"),
    USER_IS_NOT_A_STUDENT("The user is not a student"),
    QUESTION_ANSWERS_NOT_FOUND("The student does not have an answer for that question"),
    USER_ID_IS_NULL("The user id is null"),
    CLARIFICATION_ID_IS_NULL("The clarification id is null"),
    QUESTION_ID_IS_NULL( "The question id is null"),
    QUESTION_ANSWER_ID_IS_NULL( "The answer id is null"),

    USER_IS_EMPTY("User doesn't exist."),
    PQ_NOT_FOUND("Proposed Question not found"),
    PQ_ALREADY_EVALUATED("The Proposed Question is already evaluated"),
    JUSTIFICATION_IS_EMPTY("The justification is empty"),
    JUSTIFICATION_IS_BLANK("The justification is blank"),
    PROPQUESTION_MISSING_QUESTION("Proposed Question have empty question"),
    TOPIC_NOT_BELONGING_TO_COURSE("Topic does not belong to the course"),
    PROPQUESTION_CANT_BE_REMOVED("The Proposed Question was approved so it can't be removed"),

    ACCESS_DENIED("You do not have permission to view this resource"),
    CANNOT_OPEN_FILE("Cannot open file");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}