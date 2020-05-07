<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="55%"
    max-height="80%"
  >
    <v-card>
      <v-container grid-list-md fluid>
        <v-layout column wrap>
          <v-card-title>
            <span class="headline"> {{ 'Edit Evaluation' }} </span>
          </v-card-title>
          <v-col cols="12">
            <v-select
              outlined
              v-model="propQuestion.evaluation"
              :items="evaluationsList"
              :color="getEvaluationColor(propQuestion.evaluation)"
              small
              data-cy="evaluation"
            >
              <span>{{ propQuestion.evaluation }}</span>
            </v-select>
          </v-col>

          <v-card-text class="text-left" v-if="propQuestion">
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="5"
                v-model="propQuestion.justification"
                label="Justification"
                data-cy="justification"
              ></v-textarea>
            </v-flex>
          </v-card-text>

          <v-card-actions>
            <v-spacer />
            <v-btn
              color="blue darken-1"
              @click="$emit('dialog', false)"
              data-cy="cancelButton"
              >Cancel</v-btn
            >
            <v-btn v-if="!(!propQuestion.justification && propQuestion.evaluation ==='REJECTED')"
              color="blue darken-1"
              @click="saveEvaluation"
              data-cy="saveButton"
              >Save</v-btn
            >
          </v-card-actions>
        </v-layout>
      </v-container>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import Store from '@/store';

@Component
export default class EditJustificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ProposedQuestion, required: true })
  readonly propQuestion!: ProposedQuestion;
  evaluationsList = ['AWAITING', 'APPROVED', 'REJECTED'];

  async saveEvaluation() {
    this.propQuestion.teacher = Store.getters.getUser;
    try {
      const result = await RemoteServices.evaluate(this.propQuestion);
      this.$emit('save-evaluation', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  getEvaluationColor(evaluation: string) {
    if (evaluation === 'AWAITING') return 'grey';
    else if (evaluation === 'APPROVED') return 'green';
    else return 'red';
  }
}
</script>
