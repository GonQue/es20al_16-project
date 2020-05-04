export default class StatementClarificationQuestion {
  id: number | null = null;
  content: string = '';
  answerId: number | null = null;
  questionContent: string = '';
  status: string = 'NOT_ANSWERED';
  needClarification: boolean | null = null;
  availableToOtherStudents: boolean | null = null;
  creationDate!: string | null;

  constructor(jsonObj?: StatementClarificationQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.content = jsonObj.content;
      this.answerId = jsonObj.answerId;
      this.questionContent = jsonObj.questionContent;
      this.status = jsonObj.status;
      this.needClarification = jsonObj.needClarification;
      this.availableToOtherStudents = jsonObj.availableToOtherStudents;
      this.creationDate = jsonObj.creationDate;
    }
  }
}
