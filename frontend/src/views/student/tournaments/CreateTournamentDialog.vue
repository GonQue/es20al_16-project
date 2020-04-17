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
              Create Tournament
            </span>
            </v-card-title>


            <v-card-text class="text-left" v-if="createTournament">
                <v-container grid-list-md fluid>
                    <v-layout column wrap>
                        <v-flex xs24 sm12 md8>
                            <v-text-field v-model="createTournament.name" label="Title" />
                            <p>Name is {{createTournament.name}}</p>
                        </v-flex>
                        <v-row>
                            <v-col cols="12" sm="6">
                                <v-datetime-picker
                                        label="*Start Date"
                                        format="yyyy-MM-dd HH:mm"
                                        v-model="createTournament.startDate"
                                        date-format="yyyy-MM-dd"
                                        time-format="HH:mm"
                                >
                                </v-datetime-picker>
                            </v-col>
                            <v-spacer></v-spacer>
                            <v-col cols="12" sm="6">
                                <v-datetime-picker
                                        label="End Date"
                                        format="yyyy-MM-dd HH:mm"
                                        v-model="createTournament.endDate"
                                        date-format="yyyy-MM-dd"
                                        time-format="HH:mm"
                                >
                                </v-datetime-picker>
                            </v-col>
                        </v-row>

                       <v-col cols="12">
                           <v-slider
                            v-model="createTournament.numberOfQuestions"
                            step="1"
                            label="Number of questions"
                            thumb-label="always"></v-slider>
                       </v-col>



                        <v-autocomplete
                                v-model="createTournament.topics"
                                :items="topics"
                                multiple
                                return-object
                                item-text="name"
                                item-value="name"
                                label="Topics"
                        >
                            <template v-slot:selection="data">
                                <v-chip
                                        v-bind="data.attrs"
                                        :input-value="data.selected"
                                        close
                                        @click="data.select"
                                        @click:close="remove(data.item)"
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




                    </v-layout>
                </v-container>
            </v-card-text>


            <v-card-actions>
                <v-spacer />
                <v-btn
                        color="blue darken-1"
                        @click="$emit('close-dialog')"
                        data-cy="cancelButton"
                >Cancel</v-btn
                >
                <v-btn color="blue darken-1" @click="saveTournament" data-cy="saveButton"
                >Save</v-btn
                >
            </v-card-actions>

        </v-card>
    </v-dialog>

</template>




<script lang="ts">

import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import { Tournament } from '@/models/user/Tournament';
import RemoteServices from '@/services/RemoteServices';
import { Quiz } from '@/models/management/Quiz';
import Topic from '@/models/management/Topic';
import StatementQuiz from '@/models/statement/StatementQuiz';


@Component
export default class CreateTournamentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;
  @Prop({ type: Array, required: true }) readonly topics!: Topic;

  createTournament!: Tournament;
  //topics: Topic[] = [];
  //quizzes: Quiz[] = [];//JSON.parse(JSON.stringify(this.question.topics));
  quizzes: StatementQuiz[] = [];

  async created() {
    this.createTournament = new Tournament(this.tournament);
    this.createTournament.status = 'CREATED';
    /*await this.$store.dispatch('loading');
    try {
      this.quizzes = await RemoteServices.getNonGeneratedQuizzes();

    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');*/
  }


  async saveTournament() {
    console.log(this.createTournament);
    if (
      this.createTournament &&
      (!this.createTournament.name ||
        !this.createTournament.startDate ||
        !this.createTournament.endDate)
    ) {
      await this.$store.dispatch(
        'error',
        'Tournament missing parameters'
      );
      return;
    }

    if (this.createTournament && this.createTournament.id == null) {
      try {
        const result = await RemoteServices.createTournament(this.createTournament);
        this.$emit('new-tournament', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

};
</script>

<style scoped></style>
