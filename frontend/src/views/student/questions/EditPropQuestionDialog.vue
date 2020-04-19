<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
    data-cy="dialog"
  >
    <v-card>
      <v-card-title>
        <span class="headline">New Proposed Question</span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editPropQuestion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="editPropQuestion.question.title"
                label="Title"
                data-cy="Title"
              />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="5"
                v-model="editPropQuestion.question.content"
                label="Question"
                data-cy="Question"
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
                :label="`Correct ${index}`"
                data-cy="CorrectOption"
              />
              <v-textarea
                outline
                rows="1"
                v-model="editPropQuestion.question.options[index - 1].content"
                :label="`Option ${index}`"
                data-cy="Option"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-layout row>
        <v-col cols="5" offset="1">
          <span
            style="position: relative; top: 45%; right: 60%; font-size: large"
            >Topics:</span
          >
          <v-form>
            <v-autocomplete
              v-model="questionTopics"
              :items="topics"
              multiple
              return-object
              item-text="name"
              item-value="name"
              @change="selectTopics"
            >
              <template v-slot:selection="data">
                <v-chip
                  v-bind="data.attrs"
                  :input-value="data.selected"
                  close
                  @click="data.select"
                  @click:close="removeTopic(data.item)"
                >
                  {{ data.item.name }}
                </v-chip>
              </template>
              <template v-slot:item="data">
                <v-list-item-content>
                  <v-list-item-title v-html="data.item.name" />
                </v-list-item-content>
              </template>
            </v-autocomplete>
          </v-form>
        </v-col>
      </v-layout>

      <v-layout row>
        <v-col cols="5" offset="1">
          <span
            style="position: relative; top: 45%; right: 60%; font-size: large"
            >Image:</span
          >
          <v-file-input
            show-size
            dense
            small-chips
            @change="saveImage($event)"
            accept="image/*"
          />
        </v-col>
      </v-layout>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="savePropQuestion"
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
import ProposedQuestion from '@/models/management/ProposedQuestion';
import Topic from '@/models/management/Topic';
import Image from '@/models/management/Image';

@Component
export default class EditPropQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ProposedQuestion, required: true })
  readonly proposedQuestion!: ProposedQuestion;

  editPropQuestion!: ProposedQuestion;
  topics: Topic[] = [];
  questionTopics: Topic[] = [];
  image: File | null = null;

  async created() {
    this.editPropQuestion = new ProposedQuestion(this.proposedQuestion);
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  selectTopics() {
    this.editPropQuestion.question.topics = this.questionTopics;
  }

  removeTopic(topic: Topic) {
    this.questionTopics = this.questionTopics.filter(
      element => element.id != topic.id
    );
  }

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

    if (this.editPropQuestion) {
      try {
        const result = await RemoteServices.createProposedQuestion(
          this.editPropQuestion
        );
        if (this.image != null && result.question.id) {
          const imageURL = await RemoteServices.uploadImage(
            this.image,
            result.question.id
          );
          this.editPropQuestion.question.image = new Image();
          this.editPropQuestion.question.image.url = imageURL;
          confirm('Image ' + imageURL + ' was uploaded!');
        }
        this.$emit('save-proposed-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async saveImage(file: File) {
    this.image = file;
  }
}
</script>
