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

      <template v-slot:item.needClarification="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              :color="needClarificationColor(item.needClarification)"
              v-on="on"
              >{{ needClarificationIcon(item.needClarification) }}</v-icon
            >
          </template>
          <span>{{ needClarificationSpan(item.needClarification) }}</span>
        </v-tooltip>
      </template>

      <template v-slot:item.availableToOtherStudents="{ item }">
        <div
          v-on:click="changeClarificationAvailability(item.id)"
          data-cy="AvailabilityDiv"
        >
          <v-switch
            data-cy="AvailabilitySwitch"
            v-model="availability"
            :value="item.id"
          ></v-switch>
        </div>
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

      <template v-slot:item.createResponse="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="newClarificationResponse(item)"
              data-cy="AnswerClarification"
              >edit
            </v-icon>
          </template>
          <span>Answer Clarification</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <edit-clarification-response-dialog
      v-if="currentClarificationQuestion"
      v-model="editClarificationResponseDialog"
      :clarification-question="currentClarificationQuestion"
      v-on:save-clarification-response="onSaveClarificationResponse"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import StatementClarificationQuestion from '@/models/statement/StatementClarificationQuestion';
import RemoteServices from '@/services/RemoteServices';
import EditClarificationResponseDialog from '@/views/teacher/clarifications/EditClarificationResponseDialog.vue';
import ClarificationResponse from '@/models/management/ClarificationResponse';
import StatementClarificationResponse from '@/models/statement/StatementClarificationResponse';
import ClarificationQuestion from '@/models/management/ClarificationQuestion';
import ListClarificationResponses from '@/views/teacher/clarifications/ListClarificationResponsesView.vue';

@Component({
  components: {
    'edit-clarification-response-dialog': EditClarificationResponseDialog,
    'list-clarification-responses': ListClarificationResponses
  }
})
export default class ListTeacherClarificationQuestionsView extends Vue {
  @Prop(ClarificationQuestion) clarificationQuestion!: ClarificationQuestion;
  clarificationResponses: StatementClarificationResponse[] = [];
  clarificationQuestions: StatementClarificationQuestion[] = [];
  editClarificationResponseDialog: boolean = false;
  currentClarificationQuestion: ClarificationQuestion | null = null;
  currentClarificationResponse: ClarificationResponse | null = null;
  availability: number[] = [];
  search: string = '';
  headers: object = [
    {
      text: 'Clarification Content',
      value: 'content',
      align: 'center',
      width: '30%'
    },
    {
      text: 'Question To Be Clarified',
      value: 'questionContent',
      align: 'center',
      width: '30%'
    },
    { text: 'Answered?', value: 'status', align: 'center', width: '5%' },
    {
      text: 'Clarified?',
      value: 'needClarification',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Public?',
      value: 'availableToOtherStudents',
      align: 'center',
      width: '5%'
    },
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
      text: 'Answer',
      value: 'createResponse',
      align: 'center',
      width: '5%'
    }
  ];

  @Watch('editClarificationResponseDialog')
  closeError() {
    if (!this.editClarificationResponseDialog) {
      this.currentClarificationResponse = null;
    }
  }

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

  needClarificationIcon(needClarification: boolean) {
    if (needClarification) return 'mdi-comment-remove';
    else return 'mdi-comment-check';
  }

  needClarificationColor(needClarification: boolean) {
    if (needClarification) return '#D32F2F';
    else return '#2E7D32';
  }

  needClarificationSpan(needClarification: boolean) {
    if (needClarification) return 'Needs clarification';
    else return 'Already clarified';
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.clarificationQuestions = await RemoteServices.getAllClarificationQuestions();
      for (let i = 0; i < this.clarificationQuestions.length; i++) {
        let num = this.clarificationQuestions[i].id;
        if (
          num != null &&
          this.clarificationQuestions[i].availableToOtherStudents
        )
          this.availability.push(num);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async showResponse(clarificationQuestion: ClarificationQuestion) {
    this.currentClarificationQuestion = clarificationQuestion;
    if (this.currentClarificationQuestion.id != null)
      await this.$router.push({
        name: 'show-teacher-clarification-responses',
        params: {
          clarificationQuestionId: this.currentClarificationQuestion.id.toString()
        }
      });
  }

  newClarificationResponse(clarificationQuestion: ClarificationQuestion) {
    this.currentClarificationQuestion = clarificationQuestion;
    this.editClarificationResponseDialog = true;
  }

  async onSaveClarificationResponse(
    clarificationResponse: ClarificationResponse
  ) {
    this.clarificationResponses = this.clarificationResponses.filter(
      cR => cR.id !== clarificationResponse.id
    );
    this.clarificationResponses.unshift(clarificationResponse);
    this.editClarificationResponseDialog = false;
    this.currentClarificationQuestion = null;

    this.availability = [];
    try {
      this.clarificationQuestions = await RemoteServices.getAllClarificationQuestions();
      for (let i = 0; i < this.clarificationQuestions.length; i++) {
        let num = this.clarificationQuestions[i].id;
        if (
          num != null &&
          this.clarificationQuestions[i].availableToOtherStudents
        )
          this.availability.push(num);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changeClarificationAvailability(clarificationQuestionId: number) {
    try {
      await RemoteServices.changeClarificationAvailability(
        clarificationQuestionId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped />
