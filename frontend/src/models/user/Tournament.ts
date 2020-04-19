import User from '@/models/user/User';
import Topic from '@/models/management/Topic';
import { Quiz } from '@/models/management/Quiz';

export class Tournament {
  id!: number;
  name: string = '';
  startDate: string = '';
  endDate: string = '';
  numberOfQuestions: number | null = null;
  topics: Topic[] = [];
  enrolled: User[] = [];
  //quiz!: Quiz | null=null;
  status: string = 'CREATED';

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.startDate = jsonObj.startDate;
      this.endDate = jsonObj.endDate;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      //this.quiz = new Quiz(jsonObj.quiz);
      this.status = jsonObj.status;

      this.enrolled = jsonObj.enrolled.map((user: User) => new User(user));
      this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
    }
  }
}
