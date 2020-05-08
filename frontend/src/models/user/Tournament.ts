import User from '@/models/user/User';
import Topic from '@/models/management/Topic';
import { Quiz } from '@/models/management/Quiz';

export class Tournament {
  id!: number;
  name: string = '';
  creator!: User;
  startDate: string = '';
  endDate: string = '';
  numberOfQuestions: number | null = null;
  topics: Topic[] = [];
  enrolled: string[] = [];
  status: string = 'CREATED';

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.creator = jsonObj.creator;
      this.startDate = jsonObj.startDate;
      this.endDate = jsonObj.endDate;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.status = jsonObj.status;
      this.enrolled = jsonObj.enrolled;

      this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
    }
  }
}
