<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="proposedQuestions"
      :search="search"
      mobile-breakpoint="0"
      multi-sort
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="newProposedQuestion"
            data-cy="proposeQuestionButton"
            >Propose Question</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.question.content="{ item }">
        <p @click="showQuestionDialog(item)"
      /></template>

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

      <template v-slot:item.justification="{ item }">
        <v-btn
          icon
          bottom
          v-if="item.justification"
          @click="showJustificationDialog(item)"
        >
          <v-icon small class="mr-2">visibility</v-icon>
        </v-btn>
      </template>

      <template v-slot:item.question.image="{}"> </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showQuestionDialog(item)"
              data-cy="showProposedQuestion"
              >visibility</v-icon
            >
          </template>
          <span>Show Question</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="deleteProposedQuestion(item)"
              color="red"
              data-cy="deleteProposedQuestion"
              >delete</v-icon
            >
          </template>
          <span>Delete Proposed Question</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <edit-question-dialog
      v-if="currentPropQuestion"
      v-model="editPropQuestionDialog"
      :proposedQuestion="currentPropQuestion"
      v-on:save-proposed-question="onSaveProposedQuestion"
    />
    <show-question-dialog
      v-if="currentPropQuestion"
      v-model="questionDialog"
      :question="currentPropQuestion.question"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
    <show-justification-dialog
      v-if="currentPropQuestion"
      v-model="justificationDialog"
      :proposedQuestion="currentPropQuestion"
      v-on:close-show-justification-dialog="onCloseShowJustificationDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import RemoteServices from '@/services/RemoteServices';
import ShowJustificationDialog from './ShowJustificationDialog.vue';
import EditPropQuestionDialog from '@/views/student/questions/EditPropQuestionDialog.vue';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';

@Component({
  components: {
    'show-question-dialog': ShowQuestionDialog,
    'show-justification-dialog': ShowJustificationDialog,
    'edit-question-dialog': EditPropQuestionDialog
  }
})
export default class ProposeQuestionView extends Vue {
  proposedQuestions: ProposedQuestion[] = [];
  currentPropQuestion: ProposedQuestion | null = null;
  justificationDialog: boolean = false;
  editPropQuestionDialog: boolean = false;
  questionDialog: boolean = false;
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'question.title', align: 'center' },
    { text: 'Question', value: 'question.content', align: 'left' },
    {
      text: 'Topics',
      value: 'question.topics',
      align: 'center',
      sortable: false
    },
    { text: 'Evaluation', value: 'evaluation', align: 'center' },
    {
      text: 'Justification',
      value: 'justification',
      align: 'center',
      sortable: false
    },
    { text: 'Proposal Date', value: 'question.creationDate', align: 'center' },
    { text: 'Image', value: 'question.image.url', align: 'center' },
    { text: 'Actions', value: 'action', align: 'center', sortable: false }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.proposedQuestions = await RemoteServices.getStudentProposedQuestions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getEvaluationColor(evaluation: string) {
    if (evaluation === 'AWAITING') return 'grey lighten-1';
    else if (evaluation === 'APPROVED') return 'green';
    else return 'red';
  }

  showQuestionDialog(proposedQuestion: ProposedQuestion) {
    this.currentPropQuestion = proposedQuestion;
    this.questionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }

  showJustificationDialog(propQuestion: ProposedQuestion) {
    this.currentPropQuestion = propQuestion;
    this.justificationDialog = true;
  }

  onCloseShowJustificationDialog() {
    this.justificationDialog = false;
  }

  newProposedQuestion() {
    this.currentPropQuestion = new ProposedQuestion();
    this.editPropQuestionDialog = true;
  }

  async deleteProposedQuestion(proposedQuestion: ProposedQuestion) {
    if (
      proposedQuestion.id &&
      confirm('Are you sure you want to delete this proposed question?')
    ) {
      try {
        await RemoteServices.deleteProposedQuestion(proposedQuestion.id);
        this.proposedQuestions = this.proposedQuestions.filter(
          pQuestion => pQuestion.id != proposedQuestion.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async onSaveProposedQuestion(proposedQuestion: ProposedQuestion) {
    this.proposedQuestions = this.proposedQuestions.filter(
      q => q.id !== proposedQuestion.id
    );
    this.proposedQuestions.unshift(proposedQuestion);
    this.editPropQuestionDialog = false;
    this.currentPropQuestion = null;
  }
}
</script>

<style lang="scss" scoped></style>
