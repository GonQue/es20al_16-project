<template>
  <div
    v-if="question"
    v-bind:class="[
      'question-container',
      answer.optionId === null ? 'unanswered' : '',
      answer.optionId !== null &&
      correctAnswer.correctOptionId === answer.optionId
        ? 'correct-question'
        : 'incorrect-question'
    ]"
  >
    <v-alert
      v-model="creationAlertSuccess"
      class="creationAlertSuccess"
      type="success"
      dismissible
      data-cy="SuccessMessage"
    >
      The clarification question was successfully created.
    </v-alert>
    <div class="question">
      <span
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
        class="square"
      >
        <i v-if="hover && questionOrder !== 0" class="fas fa-chevron-left" />
        <span v-else>{{ questionOrder + 1 }}</span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
      <div @click="increaseOrder" class="square">
        <i
          v-if="questionOrder !== questionNumber - 1"
          class="fas fa-chevron-right"
        />
      </div>
    </div>
    <ul class="option-list">
      <li
        v-for="(n, index) in question.options.length"
        :key="index"
        v-bind:class="[
          answer.optionId === question.options[index].optionId ? 'wrong' : '',
          correctAnswer.correctOptionId === question.options[index].optionId
            ? 'correct'
            : '',
          'option'
        ]"
      >
        <i
          v-if="
            correctAnswer.correctOptionId === question.options[index].optionId
          "
          class="fas fa-check option-letter"
        />
        <i
          v-else-if="answer.optionId === question.options[index].optionId"
          class="fas fa-times option-letter"
        />
        <span v-else class="option-letter">{{ optionLetters[index] }}</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[index].content)"
        />
      </li>
    </ul>
    <v-dialog v-model="dialog" persistent max-width="600px">
      <template v-slot:activator="{ on }">
        <v-btn
          v-on="on"
          class="clarification-btn"
          data-cy="ClarificationButton"
        >
          Ask a teacher to clarify this question
          <v-icon>create</v-icon>
        </v-btn>
      </template>
      <v-card>
        <v-card-title>
          <span class="headline">Ask a teacher to clarify this question</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="clarificationQuestion.content"
                  label="Question*"
                  required
                  data-cy="ClarificationContent"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
          <small>*indicates required field</small>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="blue darken-1"
            text
            @click="dialog = false"
            data-cy="CancelButton"
            >Cancel</v-btn
          >
          <v-btn
            v-if="clarificationQuestion.content != ''"
            color="blue darken-1"
            text
            @click="createClarificationQuestion"
            data-cy="SaveButton"
          >
            Submit
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Emit, Model, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import Image from '@/models/management/Image';
import RemoteServices from '@/services/RemoteServices';
import StatementClarificationQuestion from '@/models/statement/StatementClarificationQuestion';

@Component
export default class ResultComponent extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Model('questionOrder', Number) questionOrder: number | undefined;
  @Prop(StatementQuestion) readonly question!: StatementQuestion;
  @Prop(StatementCorrectAnswer) readonly correctAnswer!: StatementCorrectAnswer;
  @Prop(StatementAnswer) readonly answer!: StatementAnswer;
  @Prop() readonly questionNumber!: number;
  hover: boolean = false;
  optionLetters: string[] = ['A', 'B', 'C', 'D'];
  clarificationQuestion: StatementClarificationQuestion | null = null;
  creationAlertSuccess: boolean = false;

  @Emit()
  increaseOrder() {
    return 1;
  }

  @Emit()
  decreaseOrder() {
    return 1;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  created() {
    this.clarificationQuestion = new StatementClarificationQuestion();
    this.clarificationQuestion.answerId = this.answer.answerId;
    this.clarificationQuestion.questionContent = this.question.content;
    this.clarificationQuestion.status = 'NOT_ANSWERED';
    let today = new Date();
    this.clarificationQuestion.creationDate =
      today.getFullYear() +
      '-' +
      ('00' + (today.getMonth() + 1)).slice(-2) +
      '-' +
      ('00' + today.getDate()).slice(-2) +
      ' ' +
      ('00' + today.getHours()).slice(-2) +
      ':' +
      ('00' + today.getMinutes()).slice(-2);
  }

  async createClarificationQuestion() {
    try {
      await RemoteServices.createClarificationQuestion(
        this.question.questionId,
        this.clarificationQuestion
      );
      this.creationAlertSuccess = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');

    this.dialog = false;
  }
}
</script>

<style lang="scss" scoped>
.unanswered {
  .question {
    background-color: #761515 !important;
    color: #fff !important;
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.correct-question {
  .question .question-content {
    background-color: #285f23 !important;
    color: white !important;
  }
  .question .square {
    background-color: #285f23 !important;
    color: white !important;
  }
  .correct {
    .option-content {
      background-color: #299455;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #299455 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.incorrect-question {
  .question .question-content {
    background-color: #761515 !important;
    color: white !important;
  }
  .question .square {
    background-color: #761515 !important;
    color: white !important;
  }
  .wrong {
    .option-content {
      background-color: #cf2323;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #cf2323 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.clarification-btn {
  margin-top: 50px;
  margin-bottom: 50px;
}

.creationAlertSuccess {
  z-index: 9999;
  position: absolute;
  left: 20px;
  top: 80px;
  width: calc(100% - 40px);
}
</style>
