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
        </v-card-title>
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
          v-if="item.justification != null"
          @click="showJustificationDialog(item)"
        >
          <v-icon small class="mr-2">visibility</v-icon>
        </v-btn>
      </template>

      <template v-slot:item.action="{}">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on">visibility</v-icon>
          </template>
          <span>View justification</span>
        </v-tooltip>
      </template>
    </v-data-table>

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

@Component({
  components: {
    'show-justification-dialog': ShowJustificationDialog
  }
})
export default class ProposeQuestionView extends Vue {
  proposedQuestions: ProposedQuestion[] = [];
  currentPropQuestion: ProposedQuestion | null = null;
  justificationDialog: boolean = false;
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'question.title', align: 'center' },
    { text: 'Question', value: 'question.content', align: 'left' },
    { text: 'Topics', value: 'topics', align: 'center', sortable: false },
    { text: 'Evaluation', value: 'evaluation', align: 'center' },
    { text: 'Justification', value: 'justification', align: 'center', sortable: false },
    { text: 'Proposal Date', value: 'question.creationDate', align: 'center' },
    { text: 'Actions', value: 'action', align: 'center', sortable: false }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.proposedQuestions = await RemoteServices.getProposedQuestions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getEvaluationColor(evaluation: string) {
    if (evaluation === 'AWAITING') return 'grey';
    else if (evaluation === 'APPROVED') return 'green';
    else return 'red';
  }

  showJustificationDialog(propQuestion: ProposedQuestion) {
    this.currentPropQuestion = propQuestion;
    this.justificationDialog = true;
  }

  onCloseShowJustificationDialog() {
    this.justificationDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
