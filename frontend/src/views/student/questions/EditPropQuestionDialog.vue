<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editPropQuestion && editPropQuestion.id === null
              ? 'New Proposed Question'
              : 'Edit Proposed Question'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editPropQuestion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="editPropQuestion.question.title"
                label="Title"
              />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="editPropQuestion.question.content"
                label="Question"
              ></v-textarea>
            </v-flex>
            <v-flex
              xs24
              sm12
              md12
              v-for="index in editPropQuestion.question.options.length"
              :key="index"
            >
              <v-switch
                v-model="editPropQuestion.question.options[index - 1].correct"
                class="ma-4"
                label="Correct"
              />
              <v-textarea
                outline
                rows="1"
                v-model="editPropQuestion.question.options[index - 1].content"
                :label="`Option ${index}`"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="savePropQuestion">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ProposedQuestion from '@/models/management/ProposedQuestion';

@Component
export default class EditPropQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ProposedQuestion, required: true })
  readonly proposedQuestion!: ProposedQuestion;

  editPropQuestion!: ProposedQuestion;

  created() {
    this.editPropQuestion = new ProposedQuestion(this.proposedQuestion);
  }

  // TODO use EasyMDE with these configs
  // markdownConfigs: object = {
  //   status: false,
  //   spellChecker: false,
  //   insertTexts: {
  //     image: ['![image][image]', '']
  //   }
  // };

  async savePropQuestion() {
    if (
      this.editPropQuestion &&
      (!this.editPropQuestion.question.title ||
        !this.editPropQuestion.question.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    /*
    if (this.editProposedQuestion && this.editProposedQuestion.id != null) {
      try {
        const result = await RemoteServices.updateProposedQuestion(this.editPropQuestion);
        this.$emit('save-proposed-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } */
    if (this.editPropQuestion) {
      try {
        const result = await RemoteServices.createProposedQuestion(
          this.editPropQuestion
        );
        this.$emit('save-proposed-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
