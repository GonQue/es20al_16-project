export default class StatementClarificationResponse {
  id: number | null = null;
  teacherResponse: string = '';
  responseDate!: string | null;

  constructor(jsonObj?: StatementClarificationResponse) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.teacherResponse = jsonObj.teacherResponse;
      this.responseDate = jsonObj.responseDate;
    }
  }
}
