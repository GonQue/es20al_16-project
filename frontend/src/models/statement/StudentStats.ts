export default class StudentStats {
  totalQuizzes!: number;
  totalAnswers!: number;
  totalUniqueQuestions!: number;
  correctAnswers!: number;
  improvedCorrectAnswers!: number;
  totalAvailableQuestions!: number;

  uniqueCorrectAnswers!: number;
  uniqueWrongAnswers!: number;

  publicDashboard!: boolean;
  totalClarificationQuestions!: number;
  totalPublicClarificationQuestions!: number;
  totalProposedQuestions!: number;
  totalApprovedProposedQuestions!: number;
  totalTournamentsCreated!: number;
  totalTournamentsJoined!:number;
  totalPoints!:number;
  tournamentCorrectAnswersPerc!: number;

  constructor(jsonObj?: StudentStats) {
    if (jsonObj) {
      this.totalQuizzes = jsonObj.totalQuizzes;
      this.totalAnswers = jsonObj.totalAnswers;
      this.totalUniqueQuestions = jsonObj.totalUniqueQuestions;
      this.correctAnswers = jsonObj.correctAnswers;
      this.improvedCorrectAnswers = jsonObj.improvedCorrectAnswers;
      this.uniqueCorrectAnswers = jsonObj.uniqueCorrectAnswers;
      this.uniqueWrongAnswers = jsonObj.uniqueWrongAnswers;
      this.totalAvailableQuestions = jsonObj.totalAvailableQuestions;
      this.publicDashboard = jsonObj.publicDashboard;
      this.totalClarificationQuestions = jsonObj.totalClarificationQuestions;
      this.totalPublicClarificationQuestions =
        jsonObj.totalPublicClarificationQuestions;
      this.totalTournamentsCreated=jsonObj.totalTournamentsCreated;
      this.totalTournamentsJoined=jsonObj.totalTournamentsJoined;
      this.totalPoints=jsonObj.totalPoints;
      this.tournamentCorrectAnswersPerc=jsonObj.tournamentCorrectAnswersPerc;
      this.totalProposedQuestions = jsonObj.totalProposedQuestions;
      this.totalApprovedProposedQuestions =
        jsonObj.totalApprovedProposedQuestions;

    }
  }
}
