export default class StatementClarificationQuestion {
  id: number | null = null;
  content: string = '';
  answerId: number | null = null;
  questionContent: string = '';
  status: string = 'NOT_ANSWERED';
  creationDate!: string | null;

  constructor(jsonObj?: StatementClarificationQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.content = jsonObj.content;
      this.answerId = jsonObj.answerId;
      this.questionContent = jsonObj.questionContent;
      this.status = jsonObj.status;
      this.creationDate = jsonObj.creationDate;
    }
  }
}
