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
        <span class="headline">{{
          editPropQuestion && editPropQuestion.id === null
            ? 'New Proposed Question'
            : 'Edit Proposed Question'
        }}</span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editPropQuestion">
        <v-card
          class="mb-md-6"
          v-if="editPropQuestion.justification && !isTeacher"
          color="grey lighten-3"
        >
          <v-card-title>Justification</v-card-title>
          <v-card-subtitle>{{
            editPropQuestion.justification
          }}</v-card-subtitle>
        </v-card>

        <v-form lazy-validation>
          <v-text-field
            v-model="editPropQuestion.question.title"
            :rules="[rules[0]]"
            label="Title"
            required
            outlined
            data-cy="Title"
          />
          <v-textarea
            outlined
            rows="3"
            auto-grow
            required
            :rules="[rules[1]]"
            v-model="editPropQuestion.question.content"
            label="Question"
            data-cy="Question"
          ></v-textarea>
          <div
            v-for="index in editPropQuestion.question.options.length"
            :key="index"
          >
            <v-row style="margin-right: 0;">
              <v-switch
                v-model="editPropQuestion.question.options[index - 1].correct"
                class="ma-4"
                :label="`Correct ${index}`"
                data-cy="CorrectOption"
              />
              <v-textarea
                outlined
                rows="1"
                auto-grow
                v-model="editPropQuestion.question.options[index - 1].content"
                :label="`Option ${index}`"
                data-cy="Option"
              ></v-textarea>
            </v-row>
          </div>
          <v-form style="margin-left: 2.3%">
            <v-autocomplete
              v-model="questionTopics"
              :items="topics"
              multiple
              return-object
              label="Topics"
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
          <v-file-input
            show-size
            dense
            small-chips
            @change="saveImage($event)"
            accept="image/*"
            label="Image"
          />
        </v-form>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="blue-grey lighten-4"
            @click="$emit('dialog', false)"
            data-cy="cancelButton"
            >Cancel</v-btn
          >
          <v-btn
            color="blue darken-1"
            v-if="editPropQuestion.id === null && !isTeacher"
            @click="savePropQuestion"
            data-cy="saveButton"
            >Submit</v-btn
          >
          <v-btn
            color="blue darken-1"
            v-if="editPropQuestion.id !== null && !isTeacher"
            @click="savePropQuestion"
            data-cy="resubmitButton"
            >Resubmit</v-btn
          >
          <v-btn
            color="blue darken-1"
            v-if="isTeacher"
            @click="turnAvailable"
            data-cy="turnAvailable"
            >Turn Available</v-btn
          >
        </v-card-actions>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import Topic from '@/models/management/Topic';
import Store from '@/store';

@Component
export default class EditPropQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ProposedQuestion, required: true })
  readonly proposedQuestion!: ProposedQuestion;

  editPropQuestion!: ProposedQuestion;
  topics: Topic[] = [];
  questionTopics: Topic[] = [];
  image: File | null = null;
  rules: Function[] = [this.titleRequired, this.contentRequired];
  isTeacher: boolean = Store.getters.isTeacher;

  titleRequired(value: String) {
    return !!value || 'Title is required';
  }

  contentRequired(value: String) {
    return !!value || 'Question content is required';
  }

  async created() {
    this.updatePropQuestion();
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  @Watch('proposedQuestion', { immediate: true, deep: true })
  updatePropQuestion() {
    this.editPropQuestion = new ProposedQuestion(this.proposedQuestion);
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

    try {
      const result =
        this.editPropQuestion.id != null
          ? await RemoteServices.updateProposedQuestion(this.editPropQuestion)
          : await RemoteServices.createProposedQuestion(this.editPropQuestion);
      if (this.image != null && result.question.id) {
        const imageURL = await RemoteServices.uploadImage(
          this.image,
          result.question.id
        );
        confirm('Image ' + imageURL + ' was uploaded!');
      }
      this.$emit('save-proposed-question', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async saveImage(file: File) {
    this.image = file;
  }

  async turnAvailable() {
    this.editPropQuestion.teacher = Store.getters.getUser;
    try {
      const result = await RemoteServices.turnAvailable(this.editPropQuestion);
      this.$emit('available', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
