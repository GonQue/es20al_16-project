import Question from '@/models/management/Question';

export default class ProposedQuestion {
  id: number | null = null;
  question: Question = new Question();
  justification: string | null = null;
  evaluation: string = 'AWAITING';

  constructor(jsonObj?: ProposedQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.question = new Question(jsonObj.question);
      this.justification = jsonObj.justification;
      this.evaluation = jsonObj.evaluation;
    }
  }
}
