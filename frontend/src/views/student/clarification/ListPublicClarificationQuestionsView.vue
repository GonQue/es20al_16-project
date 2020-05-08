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
  @Prop(String) readonly questionId!: string;
  currentClarificationQuestion: ClarificationQuestion | null = null;
  clarificationQuestions: StatementClarificationQuestion[] = [];
  search: string = '';
  headers: object = [
    {
      text: 'Clarification Content',
      value: 'content',
      align: 'center',
      width: '40%'
    },
    {
      text: 'Question To Be Clarified',
      value: 'questionContent',
      align: 'center',
      width: '40%'
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
    }
  ];

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
        name: 'show-public-student-clarification-responses',
        params: {
          clarificationQuestionId: this.currentClarificationQuestion.id.toString()
        }
      });
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.clarificationQuestions = await RemoteServices.getPublicClarificationQuestions(
        parseInt(this.questionId)
      );
    } catch (error) {
      if (this.questionId != null) await this.$store.dispatch('error', error);
      else
        await this.$router.push({
          name: 'solved-quizzes'
        });
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped />
