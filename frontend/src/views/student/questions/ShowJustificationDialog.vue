<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline" v-if="proposedQuestion.teacher">
          Justification from {{ proposedQuestion.teacher.name }}</span
        >
      </v-card-title>
      <v-card-text class="text-left">
        <span>{{ proposedQuestion.justification }}</span>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Vue, Model, Prop, Component } from 'vue-property-decorator';
import ProposedQuestion from '@/models/management/ProposedQuestion';

@Component
export default class ShowJustificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ProposedQuestion, required: true })
  readonly proposedQuestion!: ProposedQuestion;
}
</script>
