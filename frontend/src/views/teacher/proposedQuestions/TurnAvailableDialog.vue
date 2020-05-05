<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="55%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline"> Turn Question Available </span>
      </v-card-title>

      <v-card-actions>
        <v-btn
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="turnAvailable"
          data-cy="turnAvailable"
          >Turn Available</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import Store from '@/store';

@Component
export default class TurnAvailableDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ProposedQuestion, required: true })
  readonly propQuestion!: ProposedQuestion;

  async turnAvailable() {
    this.propQuestion.teacher = Store.getters.getUser;
    try {
      const result = await RemoteServices.turnAvailable(this.propQuestion);
      this.$emit('available', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
