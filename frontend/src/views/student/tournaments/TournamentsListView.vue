<template>
  <v-card class="table">
    <v-data-table
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

      <!-- <template v-slot:item.action="{ item }">
        <template v-slot:activator="{ on }">
          <v-btn color="primary" dark @click="enrollStudent(item)" data-cy="enrollButton" v-on="on" v-show="!enrollStudentBool">Enroll</v-btn>
          <v-btn color="primary" dark @click="enrollStudent(item)" data-cy="enrollButton" v-on="on" v-show="enrollStudentBool">Enrolled</v-btn>
        </template>
      </template> -->
      <template v-slot:item.enrollment="{ item }">
          <v-tooltip>
              <template v-slot:activator="{ on }">
                <v-btn color="primary" dark @click="enrolled(item)" data-cy="enrollButton" v-on="on" v-show="f(item)">Enroll</v-btn>
                <v-btn color="primary" dark @click="enrolled(item)" data-cy="enrollButton" v-on="on" v-show="!f(item)">Enrolled</v-btn>
              </template>
          </v-tooltip>
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
   /* data: {
      firstname: 'Luca';
      lastname: 'Rossi';
    } | null=null;
    computed: {
      fullname: function() {
      return this.firstname + ' ' + this.lastname;
    }
    } | null=null;*/
    //enrollStudentBool:boolean=true;
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
      {
        text: 'Enrollment',
        value: 'enrollment',
        align: 'center',
        width: '7%',
        sortable: false
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

    async onCloseDialog() {
      this.createTournamentDialog = false;
      this.tournament = null;
    }

    async onCreateTournament(tournament: Tournament){
      this.tournaments.unshift(tournament);
      this.createTournamentDialog = false;
      this.tournament = null;
    }
    async enrolled(tournament:Tournament){
      try {
        await RemoteServices.enrollStudent(tournament);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
    f(tournament:Tournament):boolean{
      let user= this.$store.getters.getUser;
      let usersMap = tournament.enrolled;
      for(let u in usersMap){
        if(u==user.username){
          return true;
        }
      }
      return false;
    }



  }
</script>

<style lang="scss" scoped></style>
