<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          Answer Clarification
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editClarificationResponse">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="editClarificationResponse.teacherResponse"
                label="Response"
                data-cy="TeacherResponse"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue-grey lighten-5"
          @click="$emit('dialog', false)"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="saveClarificationResponse"
          data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
import ClarificationResponse from '@/models/management/ClarificationResponse';
import ClarificationQuestion from '@/models/management/ClarificationQuestion';

@Component
export default class EditClarificationResponseDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ClarificationQuestion, required: true })
  readonly clarificationQuestion!: ClarificationQuestion;
  @Prop({ type: ClarificationResponse, required: true })
  readonly clarificationResponse!: ClarificationResponse;

  editClarificationResponse!: ClarificationResponse;

  created() {
    this.editClarificationResponse = new ClarificationResponse(
      this.clarificationResponse
    );
  }

  async saveClarificationResponse() {
    if (
      this.editClarificationResponse &&
      !this.editClarificationResponse.teacherResponse
    ) {
      await this.$store.dispatch('error', 'The response cannot be empty');
      return;
    }
    if (this.editClarificationResponse) {
      try {
        const result = await RemoteServices.createClarificationResponse(
          this.clarificationQuestion.id,
          this.editClarificationResponse
        );
        this.$emit('save-clarification-response', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
