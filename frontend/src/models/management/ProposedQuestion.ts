import Question from '@/models/management/Question';
import Store from '@/store';
import User from '@/models/user/User';

export default class ProposedQuestion {
  id: number | null = null;
  student: User = Store.getters.getUser;
  teacher: User | null = null;
  question: Question = new Question();
  justification: string | null = null;
  evaluation: string = 'AWAITING';

  constructor(jsonObj?: ProposedQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.student = jsonObj.student;
      if (jsonObj.teacher) {
        this.teacher = jsonObj.teacher;
      }
      this.question = new Question(jsonObj.question);
      this.justification = jsonObj.justification;
      this.evaluation = jsonObj.evaluation;
    }
  }
}
