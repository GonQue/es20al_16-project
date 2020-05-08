<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="40%"
  >
    <v-card>
      <v-card-title v-if="proposedQuestion.teacher">
        Justification from {{ proposedQuestion.teacher.name }}
      </v-card-title>
      <v-card-text class="text-left">
        {{ proposedQuestion.justification }}
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          dark
          @click="editProposedQuestion"
          data-cy="editPropQuestionButton"
          >Edit Question</v-btn
        >
      </v-card-actions>
    </v-card>
    <edit-question-dialog
      v-if="proposedQuestion"
      v-model="editPropQuestionDialog"
      :proposedQuestion="proposedQuestion"
      v-on:save-proposed-question="saveProposedQuestion"
      v-on:dialog="closeDialog"
    />
  </v-dialog>
</template>

<script lang="ts">
import { Vue, Model, Prop, Component } from 'vue-property-decorator';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import EditPropQuestionDialog from '@/views/student/questions/EditPropQuestionDialog.vue';

@Component({
  components: {
    'edit-question-dialog': EditPropQuestionDialog
  }
})
export default class ShowJustificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ProposedQuestion, required: true })
  readonly proposedQuestion!: ProposedQuestion;

  editPropQuestionDialog: boolean = false;

  editProposedQuestion() {
    this.editPropQuestionDialog = true;
  }

  saveProposedQuestion(proposedQuestion: ProposedQuestion) {
    this.editPropQuestionDialog = false;
    this.$emit('save-proposed-question', proposedQuestion);
  }

  closeDialog() {
    this.editPropQuestionDialog = false;
    this.$emit('dialog', false);
  }
}
</script>
