<template>
  <v-card class="table">
    <v-data-table
            data-cy="tournamentsTable"
            :headers="headers"
            :items="tournaments"
            :search="search"
            disable-pagination
            :hide-default-footer="true"
            :mobile-breakpoint="0"
            multi-sort
    >
    <template v-slot:top>
      <v-card-title>
        <v-text-field
          v-model="search"
          append-icon="search"
          label="Search"
          class="mx-2"
        />
        <v-spacer />
        <v-btn color="primary" dark @click="createTournament" data-cy="createButton">New Tournament</v-btn>

      </v-card-title>
    </template>


    </v-data-table>
    <create-tournament-dialog
      v-if="tournament"
      v-model="createTournamentDialog"
      :tournament="tournament"
      :topics="topics"
      v-on:new-tournament="onCreateTournament"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { Tournament } from '@/models/user/Tournament';
import CreateTournamentDialog from '@/views/student/tournaments/CreateTournamentDialog.vue';
import RemoteServices from '@/services/RemoteServices';
import Topic from '@/models/management/Topic';

@Component({
  components: {
    'create-tournament-dialog': CreateTournamentDialog,
  }
})
export default class TournamentsListView extends Vue {
  createTournamentDialog: boolean = true;
  tournament : Tournament | null = null;
  tournaments: Tournament[] = [];
  topics: Topic[] = [];
  search: string = '';
  headers: object = [
    {
      text: 'Tournament Name',
      value: 'name',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Start date',
      value: 'startDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'End date',
      value: 'endDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Question Number',
      value: 'numberOfQuestions',
      align: 'center',
      width: '10%'
    },
  ];

async created(){
  await this.$store.dispatch('loading');
  try {
    this.tournaments = (await RemoteServices.getOpenTournaments()).reverse();
    this.topics = await RemoteServices.getTopics();
  } catch (error) {
    await this.$store.dispatch('error', error);
  }
  await this.$store.dispatch('clearLoading');
}

  async createTournament() {
    this.tournament = new Tournament();
    this.createTournamentDialog = true;
  }

  onCloseDialog() {
    this.createTournamentDialog = false;
    this.tournament = null;
  }

  onCreateTournament(tournament: Tournament){
    this.tournaments.unshift(tournament);
    this.createTournamentDialog = false;
    this.tournament = null;
  }


}
</script>

<style lang="scss" scoped></style>
