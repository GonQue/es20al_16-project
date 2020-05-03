<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="clarificationQuestions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
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

      <template v-slot:item.status="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" :color="statusColor(item.status)" v-on="on">{{
              statusIcon(item.status)
            }}</v-icon>
          </template>
          <span>{{ statusSpan(item.status) }}</span>
        </v-tooltip>
      </template>

      <template v-slot:item.responses="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showResponse(item)"
              data-cy="ShowResponses"
              >mdi-comment-text-multiple</v-icon
            >
          </template>
          <span>Show Responses</span>
        </v-tooltip>
      </template>

      <template v-slot:item.deleteClarification="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="deleteClarificationQuestion(item.id)"
              color="#D32F2F"
              data-cy="DeleteClarificationIcon"
              >delete</v-icon
            >
          </template>
          <span>Remove Clarification</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import StatementClarificationQuestion from '@/models/statement/StatementClarificationQuestion';
import RemoteServices from '@/services/RemoteServices';
import ClarificationQuestion from '@/models/management/ClarificationQuestion';
import EditClarificationResponseDialog from '@/views/teacher/clarifications/EditClarificationResponseDialog.vue';
import ListClarificationResponses from '@/views/teacher/clarifications/ListClarificationResponsesView.vue';

@Component({
  components: {
    'list-clarification-responses': ListClarificationResponses
  }
})
export default class ListClarificationQuestionsView extends Vue {
  @Prop(ClarificationQuestion) clarificationQuestion!: ClarificationQuestion;
  currentClarificationQuestion: ClarificationQuestion | null = null;
  clarificationQuestions: StatementClarificationQuestion[] = [];
  search: string = '';
  headers: object = [
    {
      text: 'Clarification Content',
      value: 'content',
      align: 'center',
      width: '35%'
    },
    {
      text: 'Question To Be Clarified',
      value: 'questionContent',
      align: 'center',
      width: '35%'
    },
    { text: 'Status', value: 'status', align: 'center', width: '5%' },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center',
      width: '15%'
    },
    {
      text: 'Show Responses',
      value: 'responses',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Remove',
      value: 'deleteClarification',
      align: 'center',
      width: '5%'
    }
  ];

  statusIcon(status: string) {
    if (status == 'NOT_ANSWERED') return 'mdi-comment-remove';
    else if (status == 'ANSWERED') return 'mdi-comment-check';
    else return 'mdi-comment-remove';
  }

  statusColor(status: string) {
    if (status == 'NOT_ANSWERED') return '#D32F2F';
    else if (status == 'ANSWERED') return '#2E7D32';
    else return '#D32F2F';
  }

  statusSpan(status: string) {
    if (status == 'NOT_ANSWERED') return 'Not Answered';
    else if (status == 'ANSWERED') return 'Answered';
    else return 'Not Answered';
  }

  async deleteClarificationQuestion(clarificationId: number) {
    if (
      confirm('Are you sure you want to delete this clarification question?')
    ) {
      try {
        await RemoteServices.deleteClarificationQuestion(clarificationId);
        this.clarificationQuestions = this.clarificationQuestions.filter(
          clarificationQuestion => clarificationQuestion.id != clarificationId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async showResponse(clarificationQuestion: ClarificationQuestion) {
    this.currentClarificationQuestion = clarificationQuestion;
    if (this.currentClarificationQuestion.id != null)
      await this.$router.push({
        name: 'show-student-clarification-responses',
        params: {
          clarificationQuestionId: this.currentClarificationQuestion.id.toString()
        }
      });
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.clarificationQuestions = await RemoteServices.getClarificationQuestions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped />
