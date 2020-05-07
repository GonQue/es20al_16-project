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
      :single-expand="true"
      item-key="id"
      show-expand
      class="elevation-1"
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
          <v-btn
            color="primary"
            dark
            @click="createTournament"
            data-cy="createButton"
            >New Tournament</v-btn
          >
        </v-card-title>

      </template>

      <template v-slot:item.enrollment="{ item }">

        <v-tooltip>
          <template v-slot:activator="{ on }">
            <v-btn
              color="primary"
              @click="enrolled(item)"
              data-cy="enrollButton"
              v-on="on"
              v-show="
                !checkIfEnrolled(item) && !enrollButtons.includes(item.id)
              "
            >
              Enroll
            </v-btn>
            <v-btn
              class="white--text"
              color="green"
              v-show="checkIfEnrolled(item) || enrollButtons.includes(item.id)"
              @click="solveTournamentQuiz(item)"
              >Join</v-btn
            >
          </template>
        </v-tooltip>
      </template>
      <template v-slot:item.delete="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-show = "checkIfCreator(item)"
              v-on="on"
              @click="deleteTournament(item)"
              color="red"
              data-cy="deleteTournament"
              >delete</v-icon
            >
          </template>
          <span>Delete Tournament</span>
        </v-tooltip>
      </template>

      <template v-slot:expanded-item="{ headers, item }">
        <td :colspan="headers.length">
          <v-simple-table>
            <template v-slot:default>
              <tbody>
                <tr v-for="topic in item.topics" :key="topic.id">
                  <td>{{ topic.name }}</td>
                </tr>
              </tbody>
            </template>
          </v-simple-table>
        </td>
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
import StatementManager from '@/models/statement/StatementManager';
import ProposedQuestion from '@/models/management/ProposedQuestion';

@Component({
  components: {
    'create-tournament-dialog': CreateTournamentDialog
  }
})
export default class TournamentsListView extends Vue {
  createTournamentDialog: boolean = true;
  tournament: Tournament | null = null;
  tournaments: Tournament[] = [];
  topics: Topic[] = [];
  search: string = '';
  sucessAlert: boolean = true;
  headers: object = [
    {
      value: 'delete',
      align: 'center',
      width: '2%',
      sortable: false
    },
    {
      text: 'Tournament Name',
      value: 'name',
      align: 'center',
      width: '15%'
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
      text: 'Number of Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Number of Students',
      value: 'enrolled.length',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Enrollment',
      value: 'enrollment',
      align: 'center',
      width: '7%',
      sortable: false
    },

    { text: 'topics', value: 'data-table-expand', width: '1%' }
  ];

  enrollButtons: number[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (await RemoteServices.getOpenTournaments()).reverse();
      this.topics = await RemoteServices.getTopics();
      this.sucessAlert = true
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async createTournament() {
    this.tournament = new Tournament();
    this.createTournamentDialog = true;
  }

  async onCloseDialog() {
    this.createTournamentDialog = false;
    this.tournament = null;
  }

  async onCreateTournament(tournament: Tournament) {
    this.tournaments.unshift(tournament);
    this.createTournamentDialog = false;
    this.tournament = null;
  }

  async solveTournamentQuiz(tournament: Tournament) {
    await this.$store.dispatch('loading');
    try {
      let statementManager: StatementManager = StatementManager.getInstance;
      statementManager.statementQuiz = (await RemoteServices.getTournamentQuiz(tournament.id));
      statementManager.statementQuiz.tournamentId = tournament.id;
      console.log('List', statementManager.statementQuiz.timeToAvailability, statementManager.statementQuiz.tournamentId);
      await this.$router.push({ name: 'tournament-start' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async enrolled(tournament: Tournament) {
    try {
      await RemoteServices.enrollStudent(tournament);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
    this.enrollButtons.push(tournament.id);
  }

  checkIfEnrolled(tournament: Tournament): boolean {
    let user = this.$store.getters.getUser;
    let usersMap = tournament.enrolled;
    for (let i = 0; i < usersMap.length; i++) {
      if ( usersMap[i]!=null && usersMap[i] == user.username) {
        return true;
      }
    }
    return false;
  }

  checkIfCreator(tournament:Tournament):boolean{
    let user = this.$store.getters.getUser;
    let creator = tournament.creator;
    if(creator.username==user.username)
      return true;
    return false;
  }

  async deleteTournament(tournament: Tournament) {
    if (
      tournament.id &&
      confirm('Are you sure you want to delete this tournament?')
    ) {
      try {
        await RemoteServices.deleteTournament(tournament.id);
        this.tournaments = this.tournaments.filter(
          tourn => tourn.id != tournament.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
