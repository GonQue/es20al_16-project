import { Student } from '@/models/management/Student';
import Question from '@/models/management/Question';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';

export default class ClarificationQuestion {
  id: number | null = null;
  question: Question = new Question();
  student: Student = new Student();
  answer: QuestionAnswer = new QuestionAnswer();
  content: string = '';
  status: string = 'NOT_ANSWERED';
  creationDate!: string | null;

  constructor(jsonObj?: ClarificationQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.student = jsonObj.student;
      this.question = jsonObj.question;
      this.answer = jsonObj.answer;
      this.content = jsonObj.content;
      this.status = jsonObj.status;
      this.creationDate = jsonObj.creationDate;
    }
  }
}
