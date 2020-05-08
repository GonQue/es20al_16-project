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
  needClarification: boolean | null = null;
  availableToOtherStudents: boolean | null = null;
  creationDate!: string | null;

  constructor(jsonObj?: ClarificationQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.student = jsonObj.student;
      this.question = jsonObj.question;
      this.answer = jsonObj.answer;
      this.content = jsonObj.content;
      this.status = jsonObj.status;
      this.needClarification = jsonObj.needClarification;
      this.availableToOtherStudents = jsonObj.availableToOtherStudents;
      this.creationDate = jsonObj.creationDate;
    }
  }
}
