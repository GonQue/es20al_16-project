<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="proposedQuestions"
      :search="search"
      :sort-by="['evaluation']"
      mobile-breakpoint="0"
      multi-sort
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
            data-cy="search"
          />
          <v-spacer />
        </v-card-title>
      </template>

      <template v-slot:item.question.topics="{ item }">
        <v-chip
          v-for="topic in item.question.topics"
          :key="topic.id"
          :color="'blue lighten-1'"
        >
          {{ topic.name }}
        </v-chip>
      </template>

      <template v-slot:item.evaluation="{ item }">
        <v-chip :color="getEvaluationColor(item.evaluation)">
          <span>{{ item.evaluation }}</span>
        </v-chip>
      </template>

      <template v-slot:item.content="{ item }">
        <p @click="showQuestionDialog(item)"
      /></template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showQuestionDialog(item)"
              >visibility</v-icon
            >
          </template>
          <span>View Question</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="evaluate(item)"
              data-cy="evaluate"
              >edit</v-icon
            >
          </template>
          <span>Evaluate</span>
        </v-tooltip>
      </template>
    </v-data-table>

    <evaluate-dialog
      v-if="currentPropQuestion"
      v-model="evaluateDialog"
      :evaluate="currentPropQuestion"
      v-on:save-evaluation="onSaveEvaluation"
    />

    <show-question-dialog
      v-if="currentPropQuestion"
      v-model="questionDialog"
      :question="currentPropQuestion.question"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import RemoteServices from '@/services/RemoteServices';
import ShowQuestionDialog from '../questions/ShowQuestionDialog.vue';
import EvaluateDialog from '@/views/teacher/proposedQuestions/EvaluateDialog.vue';
import User from '@/models/user/User';

@Component({
  components: {
    'show-question-dialog': ShowQuestionDialog,
    'evaluate-dialog': EvaluateDialog
  }
})
export default class ProposeQuestionView extends Vue {
  student: string = '';
  proposedQuestions: ProposedQuestion[] = [];
  currentQuestion: Question | null = null;
  currentPropQuestion: ProposedQuestion | null = null;
  evaluateDialog: boolean = false;
  questionDialog: boolean = false;
  justification: string = '';
  search: string = '';

  headers: object = [
    { text: 'Student', value: 'student.name', align: 'center' },
    { text: 'Title', value: 'question.title', align: 'center' },
    { text: 'Question', value: 'question.content', align: 'left' },
    {
      text: 'Topics',
      value: 'question.topics',
      align: 'center',
      sortable: false
    },
    { text: 'Evaluation', value: 'evaluation', align: 'center' },
    { text: 'Proposal Date', value: 'question.creationDate', align: 'center' },
    {
      text: 'Image',
      value: 'question.image.url',
      align: 'center',
      sortable: false
    },
    { text: 'Actions', value: 'action', align: 'center', sortable: false }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.proposedQuestions = await RemoteServices.getCourseProposedQuestions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  showQuestionDialog(propQuestion: ProposedQuestion) {
    this.currentPropQuestion = propQuestion;
    this.currentQuestion = propQuestion.question;
    this.questionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }

  evaluate(propQuestion: ProposedQuestion) {
    this.currentPropQuestion = propQuestion;
    this.evaluateDialog = true;
  }

  async onSaveEvaluation(propQuestion: ProposedQuestion) {
    this.proposedQuestions = this.proposedQuestions.filter(
      pq => pq.id !== propQuestion.id
    );
    this.proposedQuestions.unshift(propQuestion);
    this.evaluateDialog = false;
    this.currentPropQuestion = null;
  }

  getEvaluationColor(evaluation: string) {
    if (evaluation === 'AWAITING') return 'grey lighten-1';
    else if (evaluation === 'APPROVED') return 'green';
    else return 'red';
  }
}
</script>

<style lang="scss" scoped></style>
