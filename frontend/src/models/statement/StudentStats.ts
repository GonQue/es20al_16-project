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
  totalTournamentsJoined!: number;

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
      this.totalProposedQuestions = jsonObj.totalProposedQuestions;
      this.totalApprovedProposedQuestions =
        jsonObj.totalApprovedProposedQuestions;
      this.totalTournamentsCreated = jsonObj.totalTournamentsCreated;
      this.totalTournamentsJoined = jsonObj.totalTournamentsJoined;
    }
  }
}
