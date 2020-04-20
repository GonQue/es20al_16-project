import User from '@/models/user/User';
import ClarificationQuestion from '@/models/management/ClarificationQuestion';

export default class ClarificationResponse {
  id: number | null = null;
  clarificationQuestion: ClarificationQuestion = new ClarificationQuestion();
  teacher: User = new User();
  teacherResponse: string = '';
  responseDate!: string | null;

  constructor(jsonObj?: ClarificationResponse) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.clarificationQuestion = jsonObj.clarificationQuestion;
      this.teacher = jsonObj.teacher;
      this.teacherResponse = jsonObj.teacherResponse;
      this.responseDate = jsonObj.responseDate;
    }
  }
}
